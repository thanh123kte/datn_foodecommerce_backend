package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.Orders.CreateOrderDto;
import com.example.qtifood.dtos.Orders.UpdateOrderDto;
import com.example.qtifood.dtos.Orders.OrderResponseDto;
import com.example.qtifood.dtos.Orders.SalesStatsDto;
import com.example.qtifood.dtos.Orders.SalesDataPointDto;
import com.example.qtifood.dtos.Orders.TopProductDto;
import com.example.qtifood.entities.Order;
import com.example.qtifood.entities.Store;
import com.example.qtifood.entities.Address;
import com.example.qtifood.enums.OrderStatus;
import com.example.qtifood.enums.PaymentStatus;
import com.example.qtifood.enums.PaymentMethod;
import com.example.qtifood.enums.TransactionType;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.mappers.OrderMapper;
import com.example.qtifood.repositories.OrderRepository;
import com.example.qtifood.repositories.OrderItemRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.repositories.StoreRepository;
import com.example.qtifood.repositories.DriverRepository;
import com.example.qtifood.repositories.AddressRepository;
import com.example.qtifood.repositories.WishlistRepository;
import com.example.qtifood.services.OrderService;
import com.example.qtifood.services.WalletService;
import com.example.qtifood.services.FcmService;
import com.example.qtifood.services.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final BigDecimal PLATFORM_FEE_SHOP = new BigDecimal("0.12");
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final DriverRepository driverRepository;
    private final AddressRepository addressRepository;
    private final OrderMapper orderMapper;
    private final WalletService walletService;
    private final FcmService fcmService;
    private final ShippingService shippingService;
    private final WishlistRepository wishlistRepository;

    @Override
    @Transactional
    public OrderResponseDto createOrder(CreateOrderDto dto) {
        // Items can now be added later via POST /api/order-items/bulk
        // No validation for items required
        
        Order order = orderMapper.toEntity(dto);
        
        // Set customer
        order.setCustomer(userRepository.findById(dto.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found")));
        
        // Set store
        order.setStore(storeRepository.findById(dto.getStoreId())
            .orElseThrow(() -> new ResourceNotFoundException("Store not found")));
        
        // Set driver (optional)
        if (dto.getDriverId() != null) {
            order.setDriver(driverRepository.findById(dto.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found")));
        }
        
        // Set shipping address
        if (dto.getShippingAddressId() != null) {
            order.setShippingAddress(addressRepository.findById(dto.getShippingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found")));
        }

        // Set expected delivery time (ETA)
        order.setExpectedDeliveryTime(calculateExpectedDeliveryTime(order.getStore(), order.getShippingAddress()));
        
        // Set initial status
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        
        // Save order first to generate ID
        Order savedOrder = orderRepository.save(order);
        
        // If payment method is QTIWALLET, deduct from user's wallet
        if (savedOrder.getPaymentMethod() == PaymentMethod.QTIWALLET) {
            try {
                String customerId = dto.getCustomerId();
                java.math.BigDecimal amount = dto.getTotalAmount();
                
                // Record transaction: deduct from wallet (PAYMENT type)
                walletService.recordTransaction(
                    customerId,
                    TransactionType.PAYMENT,
                    amount,
                    "Payment for order #" + savedOrder.getId(),
                    String.valueOf(savedOrder.getId()),
                    "ORDER"
                );
                
                // Mark payment as success if wallet deduction succeeded
                savedOrder.setPaymentStatus(PaymentStatus.SUCCESS);
                savedOrder.setPaidAt(java.time.LocalDateTime.now());
                
                // Send FCM notification for payment deduction
                String title = "Thanh toán đơn hàng";
                String body = String.format("Bạn đã trừ %,.0f VND cho đơn hàng #%d", amount, savedOrder.getId());
                fcmService.sendNotification(
                    customerId,
                    title,
                    body,
                    "PAYMENT",
                    Map.of("orderId", String.valueOf(savedOrder.getId()), "amount", amount.toString())
                );
                log.info("[OrderService] Payment deduction successful for order={}, customerId={}, amount={}", savedOrder.getId(), customerId, amount);
                
            } catch (Exception e) {
                log.error("[OrderService] Payment deduction failed for QTIWALLET order: {}", e.getMessage());
                savedOrder.setPaymentStatus(PaymentStatus.FAILED);
                throw new RuntimeException("Payment deduction failed: " + e.getMessage(), e);
            }
        }
        
        return orderMapper.toDto(orderRepository.save(savedOrder));
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrder(Long id, UpdateOrderDto dto) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        // Update driver if provided
        if (dto.getDriverId() != null) {
            order.setDriver(driverRepository.findById(dto.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found")));
        }
        
        // Use mapper to update other fields
        orderMapper.updateOrderFromDto(dto, order);
        
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        orderRepository.delete(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long id) {
        return orderMapper.toDto(orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(orderMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
            .map(orderMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByStore(Long storeId) {
        return orderRepository.findByStoreId(storeId).stream()
            .map(orderMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByDriver(String driverId) {
        return orderRepository.findByDriverId(driverId).stream()
            .map(orderMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopProductDto> getTopSellingProducts(Integer limit) {
        int pageSize = (limit == null || limit <= 0) ? 5 : limit;
        return orderItemRepository.findTopProductsByOrderStatusIn(
                List.of(OrderStatus.DELIVERED, OrderStatus.REVIEWED),
                PageRequest.of(0, pageSize)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopProductDto> getTopSellingProductsByStore(Long storeId, Integer limit) {
        int pageSize = (limit == null || limit <= 0) ? 5 : limit;
        return orderItemRepository.findTopProductsByStoreAndOrderStatusIn(
                storeId,
                List.of(OrderStatus.DELIVERED, OrderStatus.REVIEWED),
                PageRequest.of(0, pageSize)
        );
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setOrderStatus(orderStatus);
            
            // Auto-update payment status based on order status
            if (orderStatus == OrderStatus.DELIVERED) {
                order.setPaymentStatus(PaymentStatus.SUCCESS);
            } else if (orderStatus == OrderStatus.CANCELLED) {
                order.setPaymentStatus(PaymentStatus.FAILED);
            }
            
            return orderMapper.toDto(orderRepository.save(order));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SalesStatsDto getStoreSalesStats(Long storeId, String period) {
        // Determine time range
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();
        switch (period.toLowerCase()) {
            case "daily":
                start = LocalDate.now().atStartOfDay();
                end = LocalDate.now().atTime(LocalTime.MAX);
                break;
            case "weekly":
                LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                start = startOfWeek.atStartOfDay();
                break;
            case "monthly":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                break;
            default:
                throw new IllegalArgumentException("Invalid period. Use daily, weekly, or monthly");
        }

        // Lấy thông tin store để trả về viewCount
        com.example.qtifood.entities.Store store = storeRepository.findById(storeId)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found: " + storeId));

        List<Order> orders = orderRepository.findByStoreIdAndOrderStatusInAndUpdatedAtBetween(
            storeId, List.of(OrderStatus.DELIVERED, OrderStatus.REVIEWED), start, end
        );

        long totalOrders = orders.size();

        Map<LocalDate, SalesPointAggregate> aggregates = new TreeMap<>();
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Order o : orders) {
            BigDecimal netRevenue = calculateNetRevenue(o);
            totalRevenue = totalRevenue.add(netRevenue);

            LocalDate dateKey = resolveOrderDate(o);
            SalesPointAggregate agg = aggregates.computeIfAbsent(dateKey, d -> new SalesPointAggregate());
            agg.orders = agg.orders + 1;
            agg.revenue = agg.revenue.add(netRevenue);
        }

        List<SalesDataPointDto> points = aggregates.entrySet().stream()
            .map(entry -> SalesDataPointDto.builder()
                .label(entry.getKey().toString())
                .revenue(entry.getValue().revenue)
                .orders(entry.getValue().orders)
                .build())
            .collect(Collectors.toList());

        Long likeCount = wishlistRepository.countByStoreId(storeId);

        return SalesStatsDto.builder()
            .period(period)
            .startDate(start)
            .endDate(end)
            .totalOrders(totalOrders)
            .totalRevenue(totalRevenue)
            .storeViewCount(store.getViewCount())
            .storeLikeCount(likeCount)
            .points(points)
            .build();
    }
    
    private BigDecimal calculateNetRevenue(Order order) {
        BigDecimal total = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal ship = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        BigDecimal goodsAmount = total.subtract(ship);
        if (goodsAmount.compareTo(BigDecimal.ZERO) < 0) {
            goodsAmount = BigDecimal.ZERO;
        }
        BigDecimal platformFee = goodsAmount.multiply(PLATFORM_FEE_SHOP);
        return goodsAmount.subtract(platformFee);
    }

    private LocalDate resolveOrderDate(Order order) {
        if (order.getUpdatedAt() != null) {
            return order.getUpdatedAt().toLocalDate();
        }
        if (order.getCreatedAt() != null) {
            return order.getCreatedAt().toLocalDate();
        }
        return LocalDate.now();
    }

    private static class SalesPointAggregate {
        private BigDecimal revenue = BigDecimal.ZERO;
        private long orders = 0L;
    }
    
    // ========== HELPER METHODS FOR ORDER CALCULATION ==========
 
    private LocalDateTime calculateExpectedDeliveryTime(Store store, Address address) {
        LocalDateTime now = LocalDateTime.now();
        
        // Base preparation time (minutes): default 30
        int prepMinutes = 30;
        
        // Travel time estimation using distance and average speed
        int travelMinutes = 20; // default fallback
        try {
            if (store != null && address != null 
                && store.getLatitude() != null && store.getLongitude() != null
                && address.getLatitude() != null && address.getLongitude() != null) {
                double distanceKm = shippingService.calculateDistance(
                        store.getLatitude().doubleValue(),
                        store.getLongitude().doubleValue(),
                        address.getLatitude().doubleValue(),
                        address.getLongitude().doubleValue());
                // Assume average speed 25 km/h, min 10 minutes
                double minutes = (distanceKm / 25.0) * 60.0;
                travelMinutes = (int) Math.ceil(Math.max(10.0, minutes));
            }
        } catch (Exception ignore) {
        }
        
        return now.plusMinutes(prepMinutes + travelMinutes);
        
    }

    @Override
    @Transactional
    public void updatePaymentStatus(Long orderId, com.example.qtifood.enums.PaymentStatus status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setPaymentStatus(status);
        orderRepository.save(order);
    }
}
