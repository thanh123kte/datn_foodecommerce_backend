package com.example.qtifood.dtos.Categories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoriesDto(
        @NotBlank
        @Size(max = 100)
        String name,

        String description,

        String imageUrl,

        Boolean isActive
) {}