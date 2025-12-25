package com.example.qtifood.dtos.StoreReviews;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class CreateStoreReviewDto {
    @NotNull(message = "Order ID is required")
    private Long orderId;
    
    @NotNull(message = "Store ID is required")
    private Long storeId;
    
    @NotNull(message = "Customer ID is required")
    private String customerId;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;
    
    private String comment;
}