package com.example.qtifood.dtos.Orders;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.qtifood.enums.OrderStatus;
import com.example.qtifood.enums.PaymentMethod;

@Data
public class UpdateOrderDto {
    private String driverId;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private Long adminVoucherId;
    private Long sellerVoucherId;
    private PaymentMethod paymentMethod;
    private String note;
    private LocalDateTime expectedDeliveryTime;
    private OrderStatus orderStatus;
    private String cancelReason;
    private Boolean ratingStatus;
    private LocalDateTime paidAt;
}
