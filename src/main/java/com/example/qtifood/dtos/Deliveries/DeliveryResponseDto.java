package com.example.qtifood.dtos.Deliveries;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.qtifood.enums.DeliveryStatus;

@Data
public class DeliveryResponseDto {
    private Long id;
    private Long orderId;
    private String driverId;
    private String driverName;
    private Double pickupLat;
    private Double pickupLng;
    private Double dropoffLat;
    private Double dropoffLng;
    private BigDecimal distanceKm;
    private DeliveryStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}