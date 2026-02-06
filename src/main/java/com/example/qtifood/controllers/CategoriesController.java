package com.example.qtifood.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.qtifood.dtos.Categories.CategoryResponseDto;
import com.example.qtifood.dtos.Categories.CreateCategoriesDto;
import com.example.qtifood.dtos.Categories.UpdateCategoriesDto;
import com.example.qtifood.services.CategoriesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoriesController {

    private final CategoriesService categoriesService;
    
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody CreateCategoriesDto dto) {
        CategoryResponseDto category = categoriesService.createCategories(dto);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        return ResponseEntity.ok(categoriesService.getAllCategories());
    }


    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriesService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Long id,
            @RequestBody UpdateCategoriesDto dto
    ) {
        CategoryResponseDto updated = categoriesService.updateCategory(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoriesService.deleteCategory(id);
        return ResponseEntity.ok("Category " + id + " deleted successfully.");
    }

    @PostMapping(value = "/{id}/image", consumes = "multipart/form-data")
    public ResponseEntity<CategoryResponseDto> uploadImage(
            @PathVariable Long id,
            @RequestParam("image") org.springframework.web.multipart.MultipartFile imageFile) {
        
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        CategoryResponseDto updatedCategory = categoriesService.uploadImage(id, imageFile);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<CategoryResponseDto> deleteImage(@PathVariable Long id) {
        CategoryResponseDto updatedCategory = categoriesService.deleteImage(id);
        return ResponseEntity.ok(updatedCategory);
    }

}
