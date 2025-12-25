package com.example.qtifood.dtos.OrderItems;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateOrderItemDto {
    private Integer quantity;
    private BigDecimal price;
}