package com.example.qtifood.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.qtifood.dtos.Orders.OrderResponseDto;
import com.example.qtifood.entities.Driver;
import com.example.qtifood.entities.Order;
import com.example.qtifood.entities.Wallet;
import com.example.qtifood.entities.Delivery;
import com.example.qtifood.enums.DriverStatus;
import com.example.qtifood.enums.OrderStatus;
import com.example.qtifood.enums.PaymentMethod;
import com.example.qtifood.enums.TransactionType;
import com.example.qtifood.mappers.OrderMapper;
import com.example.qtifood.enums.DeliveryStatus;
import com.example.qtifood.repositories.DriverRepository;
import com.example.qtifood.repositories.OrderRepository;
import com.example.qtifood.repositories.WalletRepository;
import com.example.qtifood.services.DriverAssignmentService;
import com.example.qtifood.services.FcmService;
import com.example.qtifood.services.WalletService;
import com.example.qtifood.services.ShippingService;
import com.example.qtifood.repositories.DeliveryRepository;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DriverAssignmentServiceImpl implements DriverAssignmentService {
    
    private static final Logger log = LoggerFactory.getLogger(DriverAssignmentServiceImpl.class);
    
    // Phí sàn cho shop (12%)
    private static final BigDecimal PLATFORM_FEE_SHOP = new BigDecimal("0.12");
    
    // Phí sàn cho driver (động theo phí ship)
    private static final BigDecimal DRIVER_FEE_LOW = new BigDecimal("0.12");    // Phí ship < 15k
    private static final BigDecimal DRIVER_FEE_MID = new BigDecimal("0.20");    // Phí ship 15k-30k
    private static final BigDecimal DRIVER_FEE_HIGH = new BigDecimal("0.35");   // Phí ship > 30k
    
    private final OrderRepository orderRepository;
    private final DriverRepository driverRepository;
    private final WalletRepository walletRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderMapper orderMapper;
    private final FcmService fcmService;
    private final WalletService walletService;
    private final ShippingService shippingService;
    
    @Override
    @Transactional
    public OrderResponseDto assignDriverToOrder(Long orderId) {
        log.info("[DriverAssignment] Starting driver assignment for order={}", orderId);
        
        // 1. Lấy thông tin đơn hàng
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        // 2. Kiểm tra trạng thái đơn hàng phải là PREPARED
        if (order.getOrderStatus() != OrderStatus.PREPARED) {
            throw new RuntimeException("Order status must be PREPARED to assign driver. Current status: " + order.getOrderStatus());
        }
        
        // 3. Kiểm tra đơn hàng chưa có tài xế
        if (order.getDriver() != null) {
            throw new RuntimeException("Order already has a driver assigned");
        }
        
        // 4. Tìm tài xế ONLINE
        List<Driver> onlineDrivers = driverRepository.findByStatus(DriverStatus.ONLINE);
        
        if (onlineDrivers.isEmpty()) {
            log.warn("[DriverAssignment] No online drivers available for order={}", orderId);
            throw new RuntimeException("Không có tài xế online. Vui lòng thử lại sau.");
        }
        
        // 5. Lọc tài xế có đủ tiền trong ví để gán trước cho đơn hàng
        BigDecimal totalAmount = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal requiredDeposit = totalAmount; // Tài xế cần ứng tiền toàn bộ đơn hàng
        
        List<Driver> eligibleDrivers = onlineDrivers.stream()
                .filter(driver -> {
                    Wallet driverWallet = walletRepository.findByUserId(driver.getId()).orElse(null);
                    if (driverWallet == null) {
                        log.warn("[DriverAssignment] Driver {} has no wallet", driver.getId());
                        return false;
                    }
                    boolean hasEnoughBalance = driverWallet.getBalance().compareTo(requiredDeposit) >= 0;
                    if (!hasEnoughBalance) {
                        log.debug("[DriverAssignment] Driver {} has insufficient balance: {} < {}", 
                                driver.getId(), driverWallet.getBalance(), requiredDeposit);
                    }
                    return hasEnoughBalance;
                })
                .toList();
        
        if (eligibleDrivers.isEmpty()) {
            log.warn("[DriverAssignment] No drivers with sufficient wallet balance for order={}", orderId);
            throw new RuntimeException("Không có tài xế có đủ số dư trong ví. Vui lòng thử lại sau.");
        }
        
        // 6. Chọn tài xế gần nhất với quán (theo latitude/longitude)
        Driver selectedDriver = findNearestDriver(eligibleDrivers, order.getStore().getLatitude(), order.getStore().getLongitude());
        
        if (selectedDriver == null) {
            // Fallback: chọn tài xế đầu tiên nếu không tính được khoảng cách
            selectedDriver = eligibleDrivers.get(0);
        }
        
        log.info("[DriverAssignment] Selected driver={} for order={}", selectedDriver.getId(), orderId);
        
        // 7. Gán tài xế cho đơn hàng
        order.setDriver(selectedDriver);
        order.setOrderStatus(OrderStatus.SHIPPING);
        
        // 8. Cập nhật trạng thái tài xế sang BUSY
        selectedDriver.setStatus(DriverStatus.BUSY);
        driverRepository.save(selectedDriver);
        
        // 9. Lưu đơn hàng
        Order savedOrder = orderRepository.save(order);
        
        // 10. Ứng tiền đơn hàng cho tài xế (deduct from driver wallet)
        try {
            String driverId = selectedDriver.getId();
            walletService.recordTransaction(
                driverId,
                TransactionType.PAYMENT,
                totalAmount,
                String.format("Ứng tiền đơn hàng #%d (sẽ hoàn sau khi giao thành công)", savedOrder.getId()),
                String.valueOf(savedOrder.getId()),
                "ORDER_ADVANCE"
            );
            log.info("[DriverAssignment] Driver advance deducted: driverId={}, amount={}, orderId={}", driverId, totalAmount, savedOrder.getId());
        } catch (Exception e) {
            log.error("[DriverAssignment] Failed to deduct driver advance for order={}: {}", savedOrder.getId(), e.getMessage());
            throw new RuntimeException("Failed to deduct driver advance: " + e.getMessage(), e);
        }
        
        // 11. Lưu thông tin tracking vào Firebase Realtime Database
        saveTrackingToFirebase(savedOrder);
        
        // 12. Gửi thông báo cho tài xế
        sendNotificationToDriver(selectedDriver, savedOrder);
        
        log.info("[DriverAssignment] Successfully assigned driver={} to order={}, status changed to SHIPPING", 
                selectedDriver.getId(), orderId);
        
        return orderMapper.toDto(savedOrder);
    }
    
    /**
     * Tìm tài xế gần nhất với quán (theo khoảng cách Haversine)
     */
    private Driver findNearestDriver(List<Driver> drivers, BigDecimal storeLat, BigDecimal storeLng) {
        if (storeLat == null || storeLng == null) {
            log.warn("[DriverAssignment] Store location not available, cannot calculate distance");
            return null;
        }
        
        // Giả sử tài xế đang ở vị trí mặc định (có thể lấy từ Firebase Realtime DB trong thực tế)
        // Ở đây tạm thời dùng driver đầu tiên
        // TODO: Lấy vị trí realtime của driver từ Firebase hoặc last known location
        
        Driver nearestDriver = drivers.get(0);
        double minDistance = Double.MAX_VALUE;
        
        for (Driver driver : drivers) {
            // TODO: Lấy vị trí hiện tại của driver từ Firebase Realtime DB
            // Tạm thời giả sử driver ở cùng vị trí với quán (khoảng cách = 0)
            // Trong thực tế, bạn cần query Firebase để lấy latitude/longitude của driver
            
            // double distance = calculateDistance(storeLat, storeLng, driverLat, driverLng);
            // if (distance < minDistance) {
            //     minDistance = distance;
            //     nearestDriver = driver;
            // }
        }
        
        log.info("[DriverAssignment] Selected nearest driver={}", nearestDriver.getId());
        return nearestDriver;
    }
    
    /**
     * Tính khoảng cách Haversine giữa 2 điểm (km)
     */
    @SuppressWarnings("unused")
    private double calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        final int R = 6371; // Bán kính trái đất (km)
        
        double latDistance = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double lonDistance = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * Lưu thông tin tracking vào Firebase Realtime Database
     */
    private void saveTrackingToFirebase(Order order) {
        try {
            DatabaseReference trackingRef = FirebaseDatabase.getInstance()
                    .getReference("order_tracking")
                    .child(String.valueOf(order.getId()));
            
            Map<String, Object> trackingData = new HashMap<>();
            trackingData.put("orderId", order.getId());
            trackingData.put("customerId", order.getCustomer().getId());
            trackingData.put("driverId", order.getDriver().getId());
            trackingData.put("driverName", order.getDriver().getFullName());
            trackingData.put("driverPhone", order.getDriver().getPhone());
            trackingData.put("driverAvatar", order.getDriver().getAvatarUrl());
            trackingData.put("vehicleType", order.getDriver().getVehicleType());
            trackingData.put("vehiclePlate", order.getDriver().getVehiclePlate());
            trackingData.put("status", order.getOrderStatus().name());
            trackingData.put("assignedAt", LocalDateTime.now().toString());
            trackingData.put("storeAddress", order.getStore().getAddress());
            trackingData.put("shippingAddress", order.getShippingAddress().getAddress());
            
            // Tính distance từ seller -> customer
            double distance = 0.0;
            try {
                if (order.getStore().getLatitude() != null && order.getStore().getLongitude() != null
                        && order.getShippingAddress().getLatitude() != null 
                        && order.getShippingAddress().getLongitude() != null) {
                    distance = shippingService.calculateDistance(
                        order.getStore().getLatitude().doubleValue(),
                        order.getStore().getLongitude().doubleValue(),
                        order.getShippingAddress().getLatitude().doubleValue(),
                        order.getShippingAddress().getLongitude().doubleValue()
                    );
                }
            } catch (Exception e) {
                log.warn("[DriverAssignment] Failed to calculate distance for order={}: {}", order.getId(), e.getMessage());
            }
            trackingData.put("distance", distance);
            
            // Thông tin địa chỉ giao hàng chi tiết
            trackingData.put("addressId", order.getShippingAddress().getId());
            trackingData.put("recipientName", order.getShippingAddress().getAddress());
            trackingData.put("recipientPhone", order.getShippingAddress().getPhone());

                // Tọa độ giao hàng (phục vụ tracking bản đồ)
                Double shippingLat = order.getShippingAddress().getLatitude() != null
                    ? order.getShippingAddress().getLatitude().doubleValue()
                    : null;
                Double shippingLng = order.getShippingAddress().getLongitude() != null
                    ? order.getShippingAddress().getLongitude().doubleValue()
                    : null;
                trackingData.put("shippingLatitude", shippingLat);
                trackingData.put("shippingLongitude", shippingLng);
            
            // Vị trí ban đầu (sẽ được cập nhật realtime từ app driver)
            Map<String, Object> driverLocation = new HashMap<>();
            driverLocation.put("latitude", 0.0);
            driverLocation.put("longitude", 0.0);
            driverLocation.put("updatedAt", LocalDateTime.now().toString());
            trackingData.put("driverLocation", driverLocation);
            
            trackingRef.setValueAsync(trackingData);
            
            log.info("[DriverAssignment] Tracking data saved to Firebase for order={}", order.getId());
        } catch (Exception e) {
            log.error("[DriverAssignment] Failed to save tracking data to Firebase: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Gửi thông báo cho tài xế
     */
    private void sendNotificationToDriver(Driver driver, Order order) {
        try {
            String title = "Đơn hàng mới";
            String body = String.format("Bạn có đơn hàng mới #%d. Hãy đến %s để lấy hàng.", 
                    order.getId(), order.getStore().getName());
            
            Map<String, String> data = new HashMap<>();
            data.put("orderId", String.valueOf(order.getId()));
            data.put("storeName", order.getStore().getName());
            data.put("storeAddress", order.getStore().getAddress());
            data.put("shippingAddress", order.getShippingAddress().getAddress());
            data.put("customerName", order.getCustomer().getFullName());
            data.put("customerPhone", order.getCustomer().getPhone());
            // Thêm title/body vào data để driver app xử lý được khi background
            data.put("title", title);
            data.put("body", body);
            
            fcmService.sendNotification(driver.getId(), title, body, "DELIVERY", data);
            
            log.info("[DriverAssignment] Notification sent to driver={}", driver.getId());
        } catch (Exception e) {
            log.error("[DriverAssignment] Failed to send notification to driver: {}", e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public void processDeliveryPayment(Long orderId) {
        log.info("[DriverAssignment] Processing delivery payment for order={}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        // Kiểm tra đơn hàng đã giao thành công
        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException("Order must be DELIVERED to process payment. Current status: " + order.getOrderStatus());
        }
        
        // Kiểm tra có tài xế
        if (order.getDriver() == null) {
            throw new RuntimeException("Order has no driver assigned");
        }
        
        BigDecimal totalAmount = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal shippingFee = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        
        // 1. Tính tiền hàng (totalAmount - shipping_fee) rồi trừ 12% phí sàn cho shop
        BigDecimal goodsAmount = totalAmount.subtract(shippingFee);
        BigDecimal shopPlatformFee = goodsAmount.multiply(PLATFORM_FEE_SHOP);
        BigDecimal shopReceiveAmount = goodsAmount.subtract(shopPlatformFee);
        
        // 2. Tính phí sàn cho driver (động theo phí ship)
        BigDecimal driverPlatformFee = calculateDriverPlatformFee(shippingFee);
        BigDecimal driverReceiveAmount = shippingFee.subtract(driverPlatformFee);
        
        // 3. Tổng phí sàn admin nhận được
        BigDecimal adminTotalFee = shopPlatformFee.add(driverPlatformFee);
        
        log.info("[DriverAssignment] Payment breakdown - Order={}, TotalAmount={}, GoodsAmount={}, ShippingFee={}, ShopFee={}%, DriverFee={}, ShopReceive={}, DriverReceive={}, AdminReceive={}", 
                orderId, totalAmount, goodsAmount, shippingFee, PLATFORM_FEE_SHOP.multiply(new BigDecimal("100")), 
                driverPlatformFee, shopReceiveAmount, driverReceiveAmount, adminTotalFee);
        
        try {
            // 4. Xử lý thanh toán dựa vào payment method
            String sellerId = order.getStore().getOwner().getId();
            String driverId = order.getDriver().getId();
            
            if (order.getPaymentMethod() == PaymentMethod.QTIWALLET) {
                // QTIWALLET: Hoàn tiền hàng + cộng tiền ship
                // Hoàn ứng tiền cho driver
                walletService.recordTransaction(
                        driverId,
                        TransactionType.REFUND,
                        totalAmount,
                        String.format("Hoàn ứng tiền đơn hàng #%d (QTIWALLET)", orderId),
                        String.valueOf(orderId),
                        "ORDER_ADVANCE_REFUND"
                );
                log.info("[DriverAssignment] Refunded driver advance: driverId={}, amount={}", driverId, totalAmount);
                
                // Cộng tiền hàng cho shop
                walletService.recordTransaction(
                        sellerId,
                        TransactionType.EARN,
                        shopReceiveAmount,
                        String.format("Doanh thu đơn hàng #%d (QTIWALLET, đã trừ phí sàn %.0f%%)", orderId, PLATFORM_FEE_SHOP.multiply(new BigDecimal("100")).doubleValue()),
                        String.valueOf(orderId),
                        "ORDER_INCOME"
                );
                log.info("[DriverAssignment] Credited shop wallet: sellerId={}, amount={}, method=QTIWALLET", sellerId, shopReceiveAmount);
                
                // Cộng tiền giao hàng cho driver
                walletService.recordTransaction(
                        driverId,
                        TransactionType.EARN,
                        driverReceiveAmount,
                        String.format("Phí giao hàng đơn #%d (QTIWALLET, đã trừ phí sàn)", orderId),
                        String.valueOf(orderId),
                        "DELIVERY_INCOME"
                );
                log.info("[DriverAssignment] Credited driver shipping fee: driverId={}, amount={}", driverId, driverReceiveAmount);
                
            } else {
                // COD (Cash/Bank Transfer): Khách trả tiền mặt cho driver
                // Hoàn ứng tiền cho driver (vì khách đã trả tiền mặt)
                walletService.recordTransaction(
                        driverId,
                        TransactionType.REFUND,
                        totalAmount,
                        String.format("Hoàn ứng tiền đơn hàng #%d (COD - khách trả mặt/chuyển khoản)", orderId),
                        String.valueOf(orderId),
                        "ORDER_ADVANCE_REFUND"
                );
                log.info("[DriverAssignment] Refunded driver advance (COD): driverId={}, amount={}", driverId, totalAmount);
                
                // Cộng tiền hàng cho shop (seller nhận tiền từ driver chuyển khoản sau)
                walletService.recordTransaction(
                        sellerId,
                        TransactionType.EARN,
                        shopReceiveAmount,
                        String.format("Doanh thu đơn hàng #%d (COD, đã trừ phí sàn %.0f%%)", orderId, PLATFORM_FEE_SHOP.multiply(new BigDecimal("100")).doubleValue()),
                        String.valueOf(orderId),
                        "ORDER_INCOME"
                );
                log.info("[DriverAssignment] Credited shop wallet (COD): sellerId={}, amount={}", sellerId, shopReceiveAmount);
                
                // Ghi nhận thu nhập phí ship COD cho driver
                walletService.recordTransaction(
                        driverId,
                        TransactionType.MANUAL_INCOME,
                        driverReceiveAmount,
                        String.format("Phí giao hàng COD đơn #%d (đã trừ phí sàn)", orderId),
                        String.valueOf(orderId),
                        "COD_SHIPPING_INCOME"
                );
                log.info("[DriverAssignment] Recorded COD shipping income: driverId={}, amount={}, orderId={}", driverId, driverReceiveAmount, orderId);
            }
            
            log.info("[DriverAssignment] Payment processed by method={}", order.getPaymentMethod());
            
            // 5. Cộng phí sàn cho admin wallet
            walletService.recordTransaction(
                    "admin",
                    TransactionType.EARN,
                    adminTotalFee,
                    String.format("Phí sàn đơn hàng #%d (shop: %.0f + driver: %.0f)", orderId, shopPlatformFee, driverPlatformFee),
                    String.valueOf(orderId),
                    "PLATFORM_FEE"
            );
            log.info("[DriverAssignment] Platform fee credited to admin: amount={} (shop: {}, driver: {})", 
                    adminTotalFee, shopPlatformFee, driverPlatformFee);
            
                // 6. Lưu lịch sử giao hàng vào deliveries
                saveDeliverySnapshot(order, goodsAmount, shippingFee, driverReceiveAmount, shopReceiveAmount);

                // 7. Xóa tracking realtime khi giao xong
                clearTrackingFromFirebase(orderId);

                // 8. Cập nhật trạng thái driver về ONLINE
            Driver driver = order.getDriver();
            driver.setStatus(DriverStatus.ONLINE);
            driverRepository.save(driver);
            
            log.info("[DriverAssignment] Payment processed successfully for order={}", orderId);
            
        } catch (Exception e) {
            log.error("[DriverAssignment] Failed to process delivery payment for order={}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Failed to process delivery payment: " + e.getMessage(), e);
        }
    }

    /**
     * Lưu snapshot vào bảng deliveries để driver xem lịch sử
     */
    private void saveDeliverySnapshot(Order order, BigDecimal goodsAmount, BigDecimal shippingFee,
                                      BigDecimal driverIncome, BigDecimal shopIncome) {
        try {
            Delivery delivery = deliveryRepository.findByOrderId(order.getId()).orElse(new Delivery());
            delivery.setOrder(order);
            delivery.setDriver(order.getDriver());
            delivery.setStatus(DeliveryStatus.COMPLETED);
            delivery.setCompletedAt(delivery.getCompletedAt() != null ? delivery.getCompletedAt() : LocalDateTime.now());
            // startedAt: lấy thời điểm gán tài xế (nếu chưa có), fallback createdAt của đơn
            if (delivery.getStartedAt() == null) {
                delivery.setStartedAt(order.getUpdatedAt() != null ? order.getUpdatedAt() : order.getCreatedAt());
            }

            // Snapshot dữ liệu
            delivery.setGoodsAmount(goodsAmount);
            delivery.setShippingFee(shippingFee);
            delivery.setDriverIncome(driverIncome);
            // Không lưu shopIncome theo yêu cầu
            delivery.setPaymentMethod(order.getPaymentMethod());
            delivery.setStoreName(order.getStore() != null ? order.getStore().getName() : null);
            delivery.setShippingAddress(order.getShippingAddress() != null ? order.getShippingAddress().getAddress() : null);
            delivery.setCustomerName(order.getCustomer() != null ? order.getCustomer().getFullName() : null);
            // Không lưu customerPhone, pickup/dropoff theo yêu cầu
            
            // Tính và lưu distance_km
            if (order.getStore().getLatitude() != null && order.getStore().getLongitude() != null
                    && order.getShippingAddress().getLatitude() != null 
                    && order.getShippingAddress().getLongitude() != null) {
                try {
                    double distanceKm = shippingService.calculateDistance(
                        order.getStore().getLatitude().doubleValue(),
                        order.getStore().getLongitude().doubleValue(),
                        order.getShippingAddress().getLatitude().doubleValue(),
                        order.getShippingAddress().getLongitude().doubleValue()
                    );
                    delivery.setDistanceKm(BigDecimal.valueOf(distanceKm));
                } catch (Exception e) {
                    log.warn("[DriverAssignment] Failed to calculate distance for order={}: {}", order.getId(), e.getMessage());
                }
            }

            deliveryRepository.save(delivery);
            log.info("[DriverAssignment] Saved delivery snapshot for order={}", order.getId());
        } catch (Exception e) {
            log.error("[DriverAssignment] Failed to save delivery snapshot for order={}: {}", order.getId(), e.getMessage());
        }
    }

    /**
     * Xóa tracking trên Firebase Realtime DB sau khi giao xong
     */
    private void clearTrackingFromFirebase(Long orderId) {
        try {
            DatabaseReference trackingRef = FirebaseDatabase.getInstance()
                    .getReference("order_tracking")
                    .child(String.valueOf(orderId));
            trackingRef.removeValueAsync();
            log.info("[DriverAssignment] Cleared realtime tracking for order={}", orderId);
        } catch (Exception e) {
            log.error("[DriverAssignment] Failed to clear realtime tracking for order={}: {}", orderId, e.getMessage());
        }
    }
    
    /**
     * Tính phí sàn cho driver theo phí ship
     * - Phí ship < 15k: 12%
     * - Phí ship 15k-30k: 20%
     * - Phí ship > 30k: 35%
     */
    private BigDecimal calculateDriverPlatformFee(BigDecimal shippingFee) {
        BigDecimal fee15k = new BigDecimal("15000");
        BigDecimal fee30k = new BigDecimal("30000");
        
        if (shippingFee.compareTo(fee15k) < 0) {
            return shippingFee.multiply(DRIVER_FEE_LOW); // 12%
        } else if (shippingFee.compareTo(fee30k) <= 0) {
            return shippingFee.multiply(DRIVER_FEE_MID); // 20%
        } else {
            return shippingFee.multiply(DRIVER_FEE_HIGH); // 35%
        }
    }
}
