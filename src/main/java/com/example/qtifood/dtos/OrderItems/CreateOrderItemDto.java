package com.example.qtifood.dtos.OrderItems;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class CreateOrderItemDto {
    // orderId will be set by server when creating order items
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    // price will be fetched from product by server to prevent manipulation
}