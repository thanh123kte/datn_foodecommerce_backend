package com.example.qtifood.dtos.Deliveries;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateDeliveryDto {
    private Long orderId;
    private String driverId;
    private BigDecimal distanceKm;
    private BigDecimal goodsAmount;
    private BigDecimal shippingFee;
    private BigDecimal driverIncome;
    private com.example.qtifood.enums.PaymentMethod paymentMethod;
    private String storeName;
    private String shippingAddress;
    private String customerName;
}