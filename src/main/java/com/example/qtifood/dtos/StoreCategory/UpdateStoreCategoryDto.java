// src/main/java/com/example/qtifood/dtos/storecategory/UpdateStoreCategoryDto.java
package com.example.qtifood.dtos.StoreCategory;

import jakarta.validation.constraints.Size;

public record UpdateStoreCategoryDto(
        @Size(max = 100) String name,
        String description,
        Long parentCategoryId  
) {}
