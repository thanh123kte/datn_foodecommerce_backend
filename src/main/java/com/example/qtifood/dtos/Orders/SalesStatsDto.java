package com.example.qtifood.dtos.Orders;

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
public class SalesStatsDto {
    private String period;           // daily | weekly | monthly
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Long storeViewCount;
    private Long storeLikeCount;
}
