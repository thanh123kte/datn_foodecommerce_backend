package com.example.qtifood.dtos.ProductImages;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private String imageUrl;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}