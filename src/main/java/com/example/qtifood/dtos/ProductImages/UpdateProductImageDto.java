package com.example.qtifood.dtos.ProductImages;

import jakarta.validation.constraints.NotBlank;

public record UpdateProductImageDto(
        @NotBlank(message = "Image URL is required")
        String imageUrl,

        Boolean isPrimary
) {}