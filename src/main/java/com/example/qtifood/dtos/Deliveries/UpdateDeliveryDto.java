package com.example.qtifood.dtos.Deliveries;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.qtifood.enums.DeliveryStatus;

@Data
public class UpdateDeliveryDto {
    private String driverId;
    private BigDecimal distanceKm;
    private BigDecimal goodsAmount;
    private BigDecimal shippingFee;
    private BigDecimal driverIncome;
    private com.example.qtifood.enums.PaymentMethod paymentMethod;
    private String storeName;
    private String shippingAddress;
    private String customerName;
    private DeliveryStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}