// src/main/java/com/example/qtifood/services/StoreCategoryService.java
package com.example.qtifood.services;

import com.example.qtifood.dtos.StoreCategory.*;

import java.util.List;

public interface StoreCategoryService {
    StoreCategoryResponseDto create(CreateStoreCategoryDto dto);
    StoreCategoryResponseDto getById(Long id);
    List<StoreCategoryResponseDto> listByStore(Long storeId);
    List<StoreCategoryResponseDto> listByCategory(Long categoryId);
    StoreCategoryResponseDto update(Long id, UpdateStoreCategoryDto dto);
    void delete(Long id);
}
