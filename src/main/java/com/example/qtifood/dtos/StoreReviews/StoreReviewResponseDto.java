package com.example.qtifood.dtos.StoreReviews;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StoreReviewResponseDto {
    private Long id;
    private Long orderId;
    private Long storeId;
    private String storeName;
    private Long customerId;
    private String customerName;
    private Integer rating;
    private String comment;
    private String imageUrl;
    private LocalDateTime createdAt;
}