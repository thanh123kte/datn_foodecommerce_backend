package com.example.qtifood.dtos.Deliveries;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateDeliveryDto {
    private Long orderId;
    private Long driverId;
    private Double pickupLat;
    private Double pickupLng;
    private Double dropoffLat;
    private Double dropoffLng;
    private BigDecimal distanceKm;
}