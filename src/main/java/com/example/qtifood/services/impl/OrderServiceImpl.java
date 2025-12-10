package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.Orders.CreateOrderDto;
import com.example.qtifood.dtos.Orders.UpdateOrderDto;
import com.example.qtifood.dtos.Orders.OrderResponseDto;
import com.example.qtifood.entities.Order;
import com.example.qtifood.enums.OrderStatus;
import com.example.qtifood.enums.PaymentStatus;
import com.example.qtifood.enums.PaymentMethod;
import com.example.qtifood.enums.TransactionType;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.mappers.OrderMapper;
import com.example.qtifood.repositories.OrderRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.repositories.StoreRepository;
import com.example.qtifood.repositories.DriverRepository;
import com.example.qtifood.repositories.AddressRepository;
import com.example.qtifood.services.OrderService;
import com.example.qtifood.services.WalletService;
import com.example.qtifood.services.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final DriverRepository driverRepository;
    private final AddressRepository addressRepository;
    private final OrderMapper orderMapper;
    private final WalletService walletService;
    private final FcmService fcmService;

    @Override
    @Transactional
    public OrderResponseDto createOrder(CreateOrderDto dto) {
        Order order = orderMapper.toEntity(dto);
        order.setCustomer(userRepository.findById(dto.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found")));
        order.setStore(storeRepository.findById(dto.getStoreId())
            .orElseThrow(() -> new ResourceNotFoundException("Store not found")));
        if (dto.getDriverId() != null) {
            order.setDriver(driverRepository.findById(dto.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found")));
        }
        if (dto.getShippingAddressId() != null) {
            order.setShippingAddress(addressRepository.findById(dto.getShippingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found")));
        }
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
    public List<OrderResponseDto> getOrdersByDriver(Long driverId) {
        return orderRepository.findByDriverId(driverId).stream()
            .map(orderMapper::toDto)
            .collect(Collectors.toList());
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
    @Transactional
    public void updatePaymentStatus(Long orderId, com.example.qtifood.enums.PaymentStatus status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setPaymentStatus(status);
        orderRepository.save(order);
    }
}
