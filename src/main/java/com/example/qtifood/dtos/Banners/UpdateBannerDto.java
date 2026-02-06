package com.example.qtifood.dtos.Banners;

import com.example.qtifood.enums.BannerStatus;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateBannerDto(
        @Size(max = 150, message = "Title must not exceed 150 characters")
        String title,

        String imageUrl,

        String description,

        BannerStatus status,

        LocalDateTime startDate,

        LocalDateTime endDate
) {}
