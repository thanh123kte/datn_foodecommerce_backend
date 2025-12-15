package com.example.qtifood.dtos.Deliveries;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverIncomeStatsDto {
    private String period; // "daily", "weekly", "monthly"
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer totalDeliveries;
    private BigDecimal totalIncome;
    private BigDecimal totalShippingFee;
    private BigDecimal totalDistance;
    private BigDecimal averageIncomePerDelivery;
    private BigDecimal averageDistancePerDelivery;
}
