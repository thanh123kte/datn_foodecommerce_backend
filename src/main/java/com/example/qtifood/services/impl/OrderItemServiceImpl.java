package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.OrderItems.CreateOrderItemDto;
import com.example.qtifood.dtos.OrderItems.UpdateOrderItemDto;
import com.example.qtifood.dtos.OrderItems.OrderItemResponseDto;
import com.example.qtifood.entities.OrderItem;
import com.example.qtifood.entities.Order;
import com.example.qtifood.entities.Product;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.mappers.OrderItemMapper;
import com.example.qtifood.repositories.OrderItemRepository;
import com.example.qtifood.repositories.OrderRepository;
import com.example.qtifood.repositories.ProductRepository;
import com.example.qtifood.services.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public OrderItemResponseDto createOrderItem(CreateOrderItemDto dto) {
        // Note: This method is for adding items to existing orders
        // For creating new orders with items, use OrderService.createOrder() instead
        // which handles the entire order creation in a single transaction
        
        OrderItem orderItem = orderItemMapper.toEntity(dto);
        
        // Set product and get current price
        Product product = productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        orderItem.setProduct(product);
        
        // Set current price from product (use discount price if available)
        BigDecimal currentPrice = product.getDiscountPrice() != null ? 
                                 product.getDiscountPrice() : 
                                 product.getPrice();
        orderItem.setPrice(currentPrice);
        
        return orderItemMapper.toDto(orderItemRepository.save(orderItem));
    }

    @Override
    @Transactional
    public OrderItemResponseDto updateOrderItem(Long id, UpdateOrderItemDto dto) {
        OrderItem orderItem = orderItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found"));
        
        orderItemMapper.updateOrderItemFromDto(dto, orderItem);
        
        return orderItemMapper.toDto(orderItemRepository.save(orderItem));
    }

    @Override
    @Transactional
    public void deleteOrderItem(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found"));
        orderItemRepository.delete(orderItem);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemResponseDto getOrderItemById(Long id) {
        return orderItemMapper.toDto(orderItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponseDto> getAllOrderItems() {
        return orderItemRepository.findAll().stream()
            .map(orderItemMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponseDto> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId).stream()
            .map(orderItemMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponseDto> getOrderItemsByProductId(Long productId) {
        return orderItemRepository.findByProductId(productId).stream()
            .map(orderItemMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteOrderItemsByOrderId(Long orderId) {
        orderItemRepository.deleteByOrderId(orderId);
    }
}