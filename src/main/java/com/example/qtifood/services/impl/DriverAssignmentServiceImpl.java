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
import com.example.qtifood.enums.DriverStatus;
import com.example.qtifood.enums.OrderStatus;
import com.example.qtifood.enums.TransactionType;
import com.example.qtifood.mappers.OrderMapper;
import com.example.qtifood.repositories.DriverRepository;
import com.example.qtifood.repositories.OrderRepository;
import com.example.qtifood.repositories.WalletRepository;
import com.example.qtifood.services.DriverAssignmentService;
import com.example.qtifood.services.FcmService;
import com.example.qtifood.services.WalletService;
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
    private final OrderMapper orderMapper;
    private final FcmService fcmService;
    private final WalletService walletService;
    
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
        BigDecimal shippingFee = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        BigDecimal requiredDeposit = shippingFee; // Tài xế cần có ít nhất bằng phí ship
        
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
        
        // 10. Lưu thông tin tracking vào Firebase Realtime Database
        saveTrackingToFirebase(savedOrder);
        
        // 11. Gửi thông báo cho tài xế
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
            
            // Thông tin địa chỉ giao hàng chi tiết
            trackingData.put("addressId", order.getShippingAddress().getId());
            trackingData.put("recipientName", order.getShippingAddress().getAddress());
            trackingData.put("recipientPhone", order.getShippingAddress().getPhone());
            
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
        
        // 1. Tính phí sàn cho shop (12% tổng giá trị đơn hàng)
        BigDecimal shopPlatformFee = totalAmount.multiply(PLATFORM_FEE_SHOP);
        BigDecimal shopReceiveAmount = totalAmount.subtract(shopPlatformFee);
        
        // 2. Tính phí sàn cho driver (động theo phí ship)
        BigDecimal driverPlatformFee = calculateDriverPlatformFee(shippingFee);
        BigDecimal driverReceiveAmount = shippingFee.subtract(driverPlatformFee);
        
        // 3. Tổng phí sàn admin nhận được
        BigDecimal adminTotalFee = shopPlatformFee.add(driverPlatformFee);
        
        log.info("[DriverAssignment] Payment breakdown - Order={}, TotalAmount={}, ShippingFee={}, ShopFee={}%, DriverFee={}, ShopReceive={}, DriverReceive={}, AdminReceive={}", 
                orderId, totalAmount, shippingFee, PLATFORM_FEE_SHOP.multiply(new BigDecimal("100")), 
                driverPlatformFee, shopReceiveAmount, driverReceiveAmount, adminTotalFee);
        
        try {
            // 4. Cộng tiền cho shop
            String sellerId = order.getStore().getOwner().getId();
            walletService.recordTransaction(
                    sellerId,
                    TransactionType.EARN,
                    shopReceiveAmount,
                    String.format("Doanh thu đơn hàng #%d (đã trừ phí sàn %.0f%%)", orderId, PLATFORM_FEE_SHOP.multiply(new BigDecimal("100")).doubleValue()),
                    String.valueOf(orderId),
                    "ORDER_INCOME"
            );
            log.info("[DriverAssignment] Credited shop wallet: sellerId={}, amount={}", sellerId, shopReceiveAmount);
            
            // 5. Cộng tiền giao hàng cho driver
            String driverId = order.getDriver().getId();
            walletService.recordTransaction(
                    driverId,
                    TransactionType.EARN,
                    driverReceiveAmount,
                    String.format("Phí giao hàng đơn #%d (đã trừ phí sàn)", orderId),
                    String.valueOf(orderId),
                    "DELIVERY_INCOME"
            );
            log.info("[DriverAssignment] Credited driver wallet: driverId={}, amount={}", driverId, driverReceiveAmount);
            
            // 6. Ghi nhận phí sàn cho admin (có thể lưu vào bảng riêng hoặc wallet admin)
            // TODO: Tạo wallet cho admin hoặc bảng platform_revenue
            log.info("[DriverAssignment] Platform fee collected: amount={} (shop: {}, driver: {})", 
                    adminTotalFee, shopPlatformFee, driverPlatformFee);
            
            // 7. Cập nhật trạng thái driver về ONLINE
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
