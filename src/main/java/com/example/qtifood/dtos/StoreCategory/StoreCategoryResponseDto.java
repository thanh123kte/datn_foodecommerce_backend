// src/main/java/com/example/qtifood/dtos/storecategory/StoreCategoryResponseDto.java
package com.example.qtifood.dtos.StoreCategory;

import java.time.LocalDateTime;

public record StoreCategoryResponseDto(
        Long id,
        Long storeId,
        String name,
        String description,
        Long categoryId,
        String categoryName,  // Tên category từ table categories
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
