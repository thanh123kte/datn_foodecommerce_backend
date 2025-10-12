package com.example.qtifood.services;

import java.util.List;

import com.example.qtifood.dtos.Categories.CreateCategoriesDto;
import com.example.qtifood.dtos.Categories.UpdateCategoriesDto;
import com.example.qtifood.dtos.Categories.CategoryResponseDto;

public interface CategoriesService {

    // Tạo mới
    CategoryResponseDto createCategories(CreateCategoriesDto dto);

    // Lấy tất cả
    List<CategoryResponseDto> getAllCategories();

    // Lấy 1 bản ghi theo id
    CategoryResponseDto getCategoryById(Long id);

    // Cập nhật
    CategoryResponseDto updateCategory(Long id, UpdateCategoriesDto dto);

    // Xoá
    void deleteCategory(Long id);
}
