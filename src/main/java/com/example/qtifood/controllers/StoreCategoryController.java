// src/main/java/com/example/qtifood/controllers/StoreCategoryController.java
package com.example.qtifood.controllers;

import com.example.qtifood.dtos.StoreCategory.*;
import com.example.qtifood.services.StoreCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store-categories")
@RequiredArgsConstructor
public class StoreCategoryController {

    private final StoreCategoryService service;

    @PostMapping
    public ResponseEntity<StoreCategoryResponseDto> create(@RequestBody @Valid CreateStoreCategoryDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreCategoryResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/isnot_deleted")
    public ResponseEntity<List<StoreCategoryResponseDto>> getAllNotDeleted() {
        return ResponseEntity.ok(service.listAllNotDeleted());
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<StoreCategoryResponseDto>> byStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(service.listByStore(storeId));
    }

    @GetMapping("/store/{storeId}/isnot_deleted")
    public ResponseEntity<List<StoreCategoryResponseDto>> byStoreNotDeleted(@PathVariable Long storeId) {
        return ResponseEntity.ok(service.listByStoreNotDeleted(storeId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<StoreCategoryResponseDto>> byCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(service.listByCategory(categoryId));
    }

    @GetMapping("/category/{categoryId}/isnot_deleted")
    public ResponseEntity<List<StoreCategoryResponseDto>> byCategoryNotDeleted(@PathVariable Long categoryId) {
        return ResponseEntity.ok(service.listByCategoryNotDeleted(categoryId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreCategoryResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateStoreCategoryDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/soft-delete")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
