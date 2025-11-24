package com.example.qtifood.mappers;

import com.example.qtifood.dtos.OrderItems.CreateOrderItemDto;
import com.example.qtifood.dtos.OrderItems.UpdateOrderItemDto;
import com.example.qtifood.dtos.OrderItems.OrderItemResponseDto;
import com.example.qtifood.entities.OrderItem;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class OrderItemMapper {
    
    public OrderItem toEntity(CreateOrderItemDto dto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(dto.getQuantity());
        // price will be set from product entity in service layer
        return orderItem;
    }
    
    public OrderItemResponseDto toDto(OrderItem orderItem) {
        OrderItemResponseDto dto = new OrderItemResponseDto();
        dto.setId(orderItem.getId());
        dto.setOrderId(orderItem.getOrder() != null ? orderItem.getOrder().getId() : null);
        dto.setProductId(orderItem.getProduct() != null ? orderItem.getProduct().getId() : null);
        dto.setProductName(orderItem.getProduct() != null ? orderItem.getProduct().getName() : null);
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        
        // Calculate total price
        if (orderItem.getQuantity() != null && orderItem.getPrice() != null) {
            dto.setTotalPrice(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }
        
        return dto;
    }
    
    public void updateOrderItemFromDto(UpdateOrderItemDto dto, OrderItem orderItem) {
        if (dto.getQuantity() != null) orderItem.setQuantity(dto.getQuantity());
        if (dto.getPrice() != null) orderItem.setPrice(dto.getPrice());
    }
}