package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.Orders.CreateOrderDto;
import com.example.qtifood.dtos.Orders.UpdateOrderDto;
import com.example.qtifood.dtos.Orders.OrderResponseDto;
import com.example.qtifood.dtos.OrderItems.CreateOrderItemDto;
import com.example.qtifood.entities.Order;
import com.example.qtifood.entities.OrderItem;
import com.example.qtifood.entities.Product;
import com.example.qtifood.entities.Store;
import com.example.qtifood.entities.Address;
import com.example.qtifood.enums.OrderStatus;
import com.example.qtifood.enums.PaymentStatus;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.mappers.OrderMapper;
import com.example.qtifood.mappers.OrderItemMapper;
import com.example.qtifood.repositories.OrderRepository;
import com.example.qtifood.repositories.OrderItemRepository;
import com.example.qtifood.repositories.ProductRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.repositories.StoreRepository;
import com.example.qtifood.repositories.DriverRepository;
import com.example.qtifood.repositories.AddressRepository;
import com.example.qtifood.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final DriverRepository driverRepository;
    private final AddressRepository addressRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public OrderResponseDto createOrder(CreateOrderDto dto) {
        // Validate required fields
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        
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
        
        // Set initial status
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        
        // Calculate totalAmount from items
        BigDecimal totalAmount = calculateTotalAmount(dto.getItems());
        order.setTotalAmount(totalAmount);
        
        // Calculate shipping fee (for now, simple logic based on store)
        BigDecimal shippingFee = calculateShippingFee(order.getStore(), order.getShippingAddress());
        order.setShippingFee(shippingFee);
        
        // Calculate expected delivery time (store prep time + delivery time)
        LocalDateTime expectedDeliveryTime = calculateExpectedDeliveryTime(order.getStore());
        order.setExpectedDeliveryTime(expectedDeliveryTime);
        
        // Save order first
        Order savedOrder = orderRepository.save(order);
        
        // Create order items in same transaction
        createOrderItems(savedOrder, dto.getItems());
        
        return orderMapper.toDto(savedOrder);
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
    
    // ========== HELPER METHODS FOR ORDER CALCULATION ==========
    
    /**
     * Calculate total amount from order items
     * Gets current price from product entities to prevent manipulation
     */
    private BigDecimal calculateTotalAmount(List<CreateOrderItemDto> items) {
        BigDecimal total = BigDecimal.ZERO;
        
        for (CreateOrderItemDto item : items) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProductId()));
            
            // Use discount price if available, otherwise use regular price
            BigDecimal itemPrice = product.getDiscountPrice() != null ? 
                                  product.getDiscountPrice() : 
                                  product.getPrice();
            
            BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }
        
        return total;
    }
    
    /**
     * Calculate shipping fee based on store and delivery address
     * For now, simple logic - can be enhanced with actual distance calculation
     */
    private BigDecimal calculateShippingFee(Store store, Address shippingAddress) {
        // Default shipping fee - can be enhanced with:
        // - Distance calculation between store and address
        // - Store specific delivery fees
        // - Time-based pricing (rush hours)
        // - Minimum order amount for free shipping
        
        if (shippingAddress == null) {
            return BigDecimal.ZERO; // Pickup order
        }
        
        // Simple logic: base fee + distance-based fee
        BigDecimal baseFee = new BigDecimal("15000"); // 15,000 VND base fee
        
        // TODO: Implement actual distance calculation
        // For now, return base fee
        return baseFee;
    }
    
    /**
     * Calculate expected delivery time based on store preparation time
     */
    private LocalDateTime calculateExpectedDeliveryTime(Store store) {
        LocalDateTime now = LocalDateTime.now();
        
        // Default: current time + 30 minutes (store prep time) + 20 minutes (delivery time)
        // Can be enhanced with:
        // - Store-specific preparation times
        // - Current order load of the store
        // - Distance to delivery address
        // - Driver availability
        
        return now.plusMinutes(50); // 30 min prep + 20 min delivery
    }
    
    /**
     * Create order items in the same transaction
     */
    private void createOrderItems(Order order, List<CreateOrderItemDto> itemDtos) {
        for (CreateOrderItemDto itemDto : itemDtos) {
            // Get product to set current price
            Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.getProductId()));
            
            // Create order item
            OrderItem orderItem = orderItemMapper.toEntity(itemDto);
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            
            // Set current price from product (use discount price if available)
            BigDecimal currentPrice = product.getDiscountPrice() != null ? 
                                     product.getDiscountPrice() : 
                                     product.getPrice();
            orderItem.setPrice(currentPrice);
            
            orderItemRepository.save(orderItem);
        }
    }
}
