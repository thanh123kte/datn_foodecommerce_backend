package com.example.qtifood.dtos.StoreReviews;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReviewImageDto {
    private Long id;
    private String imageUrl;
    private LocalDateTime createdAt;
}
