package com.example.qtifood.dtos.StoreReviews;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.example.qtifood.dtos.StoreReviews.ReviewImageDto;

@Data
public class StoreReviewResponseDto {
    private Long id;
    private Long orderId;
    private Long storeId;
    private String storeName;
    private String customerId;
    private String customerName;
    private Integer rating;
    private String comment;
    private List<ReviewImageDto> images;
    private String reply;
    private String customerAvatar;
    private LocalDateTime repliedAt;
    private LocalDateTime createdAt;
}