package com.example.qtifood.dtos.CartItems;

import com.example.qtifood.dtos.Products.ProductResponseDto;
import com.example.qtifood.dtos.Stores.StoreResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponseDto {
    
    private Long id;
    private Long customerId;
    private ProductResponseDto product;
    private StoreResponseDto store;
    private Integer quantity;
    private String note;
    private BigDecimal totalPrice; // quantity * product.price
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}