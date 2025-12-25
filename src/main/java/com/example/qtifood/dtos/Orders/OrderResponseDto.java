package com.example.qtifood.dtos.Orders;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.example.qtifood.enums.OrderStatus;
import com.example.qtifood.enums.PaymentMethod;
import com.example.qtifood.enums.PaymentStatus;
import com.example.qtifood.dtos.OrderItems.OrderItemResponseDto;

@Data
public class OrderResponseDto {
    private Long id;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String customerAvatar;
    private Long storeId;
    private String storeName;
    private String driverId;
    private String driverName;
    private String driverPhone;
    private Long shippingAddressId;
    private String shippingAddress;
    private String shippingReceiver;
    private String shippingPhone;
    private BigDecimal totalAmount;
    private BigDecimal itemsTotal;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
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
    private List<OrderItemResponseDto> items;
}
