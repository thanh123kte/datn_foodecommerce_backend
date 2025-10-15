package com.example.qtifood.dtos.Orders;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.qtifood.enums.OrderStatus;
import com.example.qtifood.enums.PaymentMethod;
import com.example.qtifood.enums.PaymentStatus;

@Data
public class OrderResponseDto {
    private Long id;
    private String customerId;
    private Long storeId;
    private Long driverId;
    private Long shippingAddressId;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private Long adminVoucherId;
    private Long sellerVoucherId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime paidAt;
    private OrderStatus orderStatus;
    private String note;
    private String cancelReason;
    private LocalDateTime expectedDeliveryTime;
    private Boolean ratingStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
