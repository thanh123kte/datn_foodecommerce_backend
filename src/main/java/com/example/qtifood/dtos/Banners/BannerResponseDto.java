package com.example.qtifood.dtos.Banners;

import com.example.qtifood.enums.BannerStatus;
import java.time.LocalDateTime;

public record BannerResponseDto(
        Long id,
        String title,
        String imageUrl,
        String description,
        BannerStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
