package com.example.qtifood.dtos.OrderItems;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateOrderItemDto {
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}