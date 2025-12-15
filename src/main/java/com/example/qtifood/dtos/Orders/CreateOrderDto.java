package com.example.qtifood.dtos.Orders;

import lombok.Data;
import java.util.List;
import com.example.qtifood.enums.PaymentMethod;
import com.example.qtifood.dtos.OrderItems.CreateOrderItemDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;
import java.math.BigDecimal;

@Data
public class CreateOrderDto {
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotNull(message = "Store ID is required")
    private Long storeId;
    private String driverId;
    private Long shippingAddressId;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private Long adminVoucherId;
    private Long sellerVoucherId;
    private PaymentMethod paymentMethod;
    
    private String note;
    
}
