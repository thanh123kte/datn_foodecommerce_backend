// src/main/java/com/example/qtifood/dtos/storecategory/CreateStoreCategoryDto.java
package com.example.qtifood.dtos.StoreCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStoreCategoryDto(
        @NotNull Long storeId,
        @NotBlank @Size(max = 100) String name,
        String description,
        Long parentCategoryId
) {}
