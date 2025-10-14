package com.example.qtifood.dtos.Categories;

import java.time.LocalDateTime;

public record CategoryResponseDto(
        Long id,
        String name,
        String description,
        String imageUrl,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
