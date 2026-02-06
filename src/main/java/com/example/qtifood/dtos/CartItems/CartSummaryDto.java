package com.example.qtifood.dtos.CartItems;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartSummaryDto {
    
    private List<CartItemResponseDto> items;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private Long storeId;
    private String storeName;
}