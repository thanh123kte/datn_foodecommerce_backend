package com.example.qtifood.dtos.Products;

import java.math.BigDecimal;

import com.example.qtifood.enums.ProductStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public record CreateProductDto(
        @NotNull(message = "Store ID is required")
        Long storeId,

        @NotNull(message = "Category ID is required")
        Long categoryId,

        Long storeCategoryId,

        @NotBlank(message = "Product name is required")
        @Size(max = 150, message = "Product name must not exceed 150 characters")
        String name,

        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        @DecimalMin(value = "0.0", message = "Discount price must be greater than or equal to 0")
        BigDecimal discountPrice,

        ProductStatus status
) {}