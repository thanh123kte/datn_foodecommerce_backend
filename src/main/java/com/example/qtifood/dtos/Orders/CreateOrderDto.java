package com.example.qtifood.dtos.Orders;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.qtifood.enums.PaymentMethod;

@Data
public class CreateOrderDto {
    private Long customerId;
    private Long storeId;
    private Long driverId;
    private Long shippingAddressId;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private Long adminVoucherId;
    private Long sellerVoucherId;
    private PaymentMethod paymentMethod;
    private String note;
    private LocalDateTime expectedDeliveryTime;
}
