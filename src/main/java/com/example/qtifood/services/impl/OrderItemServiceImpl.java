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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private static final Logger log = LoggerFactory.getLogger(OrderItemServiceImpl.class);
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public OrderItemResponseDto createOrderItem(CreateOrderItemDto dto) {
        Order order = orderRepository.findById(dto.getOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Product product = productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        OrderItem orderItem = orderItemMapper.toEntity(dto);
        orderItem.setOrder(order);
        orderItem.setProduct(product);

        return orderItemMapper.toDto(orderItemRepository.save(orderItem));
    }

    @Override
    @Transactional
    public List<OrderItemResponseDto> createOrderItems(List<CreateOrderItemDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new IllegalArgumentException("Items list cannot be empty");
        }

        List<OrderItemResponseDto> results = new ArrayList<>();
        for (CreateOrderItemDto dto : dtos) {
            Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + dto.getOrderId()));

            Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + dto.getProductId()));

            OrderItem orderItem = orderItemMapper.toEntity(dto);
            orderItem.setOrder(order);
            orderItem.setProduct(product);

            OrderItem saved = orderItemRepository.save(orderItem);
            results.add(orderItemMapper.toDto(saved));
        }
        return results;
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

    @Override
    @Transactional
    public List<OrderItemResponseDto> addItemsToOrder(Long orderId, List<CreateOrderItemDto> items) {
        // Validate order exists
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Items list cannot be empty");
        }

        List<OrderItemResponseDto> createdItems = new ArrayList<>();

        // Create each order item
        for (CreateOrderItemDto itemDto : items) {
            // Load product
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemDto.getProductId()));

            // Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
                orderItem.setPrice(itemDto.getPrice());

            // Save order item
            OrderItem savedItem = orderItemRepository.save(orderItem);
            createdItems.add(orderItemMapper.toDto(savedItem));

                log.info("[OrderItemService] Added item to order: order={}, product={}, quantity={}, price={}",
                    orderId, product.getId(), itemDto.getQuantity(), itemDto.getPrice());
        }

        // Update order total amount (recalculate all items)
        List<OrderItem> allItems = orderItemRepository.findByOrderId(orderId);
        BigDecimal newItemsTotal = allItems.stream()
            .map(item -> item.getPrice() != null && item.getQuantity() != null
                ? item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingFee = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        order.setTotalAmount(newItemsTotal.add(shippingFee));
        orderRepository.save(order);

        log.info("[OrderItemService] Updated order total: order={}, itemsTotal={}, shippingFee={}, newTotal={}",
                orderId, newItemsTotal, shippingFee, order.getTotalAmount());

        return createdItems;
    }
}