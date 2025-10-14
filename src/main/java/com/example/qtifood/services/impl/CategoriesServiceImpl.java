package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.Categories.CreateCategoriesDto;
import com.example.qtifood.dtos.Categories.UpdateCategoriesDto;
import com.example.qtifood.dtos.Categories.CategoryResponseDto;
import com.example.qtifood.entities.Categories;
import com.example.qtifood.repositories.CategoriesRepository;
import com.example.qtifood.services.CategoriesService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository categoriesRepository;

    @Override
    public CategoryResponseDto createCategories(CreateCategoriesDto dto) {
        Categories category = Categories.builder()
                .name(dto.name())
                .description(dto.description())
                .imageUrl(dto.imageUrl())
                .isActive(dto.isActive() != null ? dto.isActive() : true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return toDto(categoriesRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        return categoriesRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long id) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
        return toDto(category);
    }

    @Override
    public CategoryResponseDto updateCategory(Long id, UpdateCategoriesDto dto) {
        Categories category = categoriesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));

        if (dto.name() != null)        category.setName(dto.name());
        if (dto.description() != null) category.setDescription(dto.description());
        if (dto.imageUrl() != null)    category.setImageUrl(dto.imageUrl());
        if (dto.isActive() != null)    category.setIsActive(dto.isActive());
        category.setUpdatedAt(LocalDateTime.now());

        return toDto(categoriesRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoriesRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found: " + id);
        }
        categoriesRepository.deleteById(id);
    }

    private CategoryResponseDto toDto(Categories c) {
        return new CategoryResponseDto(
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.getImageUrl(),
                c.getIsActive(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
