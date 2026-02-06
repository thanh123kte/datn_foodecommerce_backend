package com.example.qtifood.dtos.Orders;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesDataPointDto {
    private String label;      // e.g., date string
    private BigDecimal revenue;
    private Long orders;
}
