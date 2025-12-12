package com.example.qtifood.dtos.ProductImages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductImageDto(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotBlank(message = "Image URL is required")
        String imageUrl,

        Boolean isPrimary
) {}