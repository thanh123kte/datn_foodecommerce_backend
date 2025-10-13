package com.example.qtifood.dtos.Products;

import java.math.BigDecimal;

import com.example.qtifood.enums.ProductStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public record UpdateProductDto(
        Long categoryId,

        @Size(max = 150, message = "Product name must not exceed 150 characters")
        String name,

        String description,

        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        @DecimalMin(value = "0.0", message = "Discount price must be greater than or equal to 0")
        BigDecimal discountPrice,

        ProductStatus status
) {}