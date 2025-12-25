package com.example.qtifood.dtos.Banners;

import com.example.qtifood.enums.BannerStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateBannerDto(
        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must not exceed 150 characters")
        String title,

        String imageUrl,

        String description,

        BannerStatus status,

        LocalDateTime startDate,

        LocalDateTime endDate
) {}
