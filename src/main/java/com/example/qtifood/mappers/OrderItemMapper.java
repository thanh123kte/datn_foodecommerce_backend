package com.example.qtifood.mappers;

import com.example.qtifood.dtos.OrderItems.CreateOrderItemDto;
import com.example.qtifood.dtos.OrderItems.UpdateOrderItemDto;
import com.example.qtifood.dtos.OrderItems.OrderItemResponseDto;
import com.example.qtifood.entities.OrderItem;
import com.example.qtifood.entities.ProductImage;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Comparator;

@Component
public class OrderItemMapper {
    
    public OrderItem toEntity(CreateOrderItemDto dto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setPrice(dto.getPrice());
        return orderItem;
    }
    
    public OrderItemResponseDto toDto(OrderItem orderItem) {
        OrderItemResponseDto dto = new OrderItemResponseDto();
        dto.setId(orderItem.getId());
        dto.setOrderId(orderItem.getOrder() != null ? orderItem.getOrder().getId() : null);
        dto.setProductId(orderItem.getProduct() != null ? orderItem.getProduct().getId() : null);
        dto.setProductName(orderItem.getProduct() != null ? orderItem.getProduct().getName() : null);
        
        // Attach primary product image if available
        if (orderItem.getProduct() != null && orderItem.getProduct().getProductImages() != null) {
            dto.setProductImage(buildFileUrl(getPrimaryImage(orderItem)));
        }

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

    private String getPrimaryImage(OrderItem orderItem) {
        return orderItem.getProduct().getProductImages().stream()
            .sorted(Comparator.comparing(ProductImage::getIsPrimary).reversed()
                .thenComparing(ProductImage::getId))
            .map(ProductImage::getImageUrl)
            .findFirst()
            .orElse(null);
    }

    private String buildFileUrl(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        String trimmed = path.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        if (trimmed.startsWith("/uploads/")) {
            return trimmed;
        }
        if (trimmed.startsWith("/")) {
            return "/uploads" + trimmed;
        }
        return "/uploads/" + trimmed;
    }
}
