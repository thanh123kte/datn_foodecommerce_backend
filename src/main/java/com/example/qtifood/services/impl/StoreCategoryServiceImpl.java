// src/main/java/com/example/qtifood/services/impl/StoreCategoryServiceImpl.java
package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.StoreCategory.CreateStoreCategoryDto;
import com.example.qtifood.dtos.StoreCategory.UpdateStoreCategoryDto;
import com.example.qtifood.dtos.StoreCategory.StoreCategoryResponseDto;
import com.example.qtifood.entities.Categories;
import com.example.qtifood.entities.Store;
import com.example.qtifood.entities.StoreCategory;
import com.example.qtifood.repositories.CategoriesRepository;
import com.example.qtifood.repositories.StoreCategoryRepository;
import com.example.qtifood.repositories.StoreRepository;
import com.example.qtifood.services.StoreCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreCategoryServiceImpl implements StoreCategoryService {

    private final StoreCategoryRepository repo;
    private final CategoriesRepository categoriesRepo;
    private final StoreRepository storeRepo; // <— thêm

    private StoreCategoryResponseDto toDto(StoreCategory sc) {
        return new StoreCategoryResponseDto(
                sc.getId(),
                sc.getStore() != null ? sc.getStore().getId() : null,  // <— lấy từ Store
                sc.getName(),
                sc.getDescription(),
                sc.getCategory() != null ? sc.getCategory().getId() : null,
                sc.getCategory() != null ? sc.getCategory().getName() : null,
                sc.getIsDeleted(),
                sc.getCreatedAt(),
                sc.getUpdatedAt()
        );
    }

    @Override
    public StoreCategoryResponseDto create(CreateStoreCategoryDto dto) {
        // bắt buộc có storeId
        Store store = storeRepo.findById(dto.storeId())
                .orElseThrow(() -> new IllegalArgumentException("Store not found: " + dto.storeId()));

        Categories category = null;
        if (dto.categoryId() != null) {
            category = categoriesRepo.findById(dto.categoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + dto.categoryId()));
        }

        StoreCategory sc = StoreCategory.builder()
                .store(store)
                .name(dto.name())
                .description(dto.description())
                .category(category)
                .build();

        return toDto(repo.save(sc));
    }

    @Override
    @Transactional(readOnly = true)
    public StoreCategoryResponseDto getById(Long id) {
        return repo.findById(id).map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("StoreCategory not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreCategoryResponseDto> listAllNotDeleted() {
        return repo.findAllByIsDeletedFalse()
                  .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreCategoryResponseDto> listByStore(Long storeId) {
        return repo.findAllByStore_Id(storeId)   // <— đổi sang field store
                  .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreCategoryResponseDto> listByStoreNotDeleted(Long storeId) {
        return repo.findAllByStore_IdAndIsDeletedFalse(storeId)
                  .stream().map(this::toDto).toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<StoreCategoryResponseDto> listByCategory(Long categoryId) {
        return repo.findAllByCategory_Id(categoryId)
                  .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreCategoryResponseDto> listByCategoryNotDeleted(Long categoryId) {
        return repo.findAllByCategory_IdAndIsDeletedFalse(categoryId)
                  .stream().map(this::toDto).toList();
    }


    @Override
    public StoreCategoryResponseDto update(Long id, UpdateStoreCategoryDto dto) {
        StoreCategory sc = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StoreCategory not found: " + id));

        if (dto.name() != null)        sc.setName(dto.name());
        if (dto.description() != null) sc.setDescription(dto.description());

        // ⚠️ Chỉ đổi category khi client cung cấp categoryId (không tự động xóa khi null)
        if (dto.categoryId() != null) {
            Categories category = categoriesRepo.findById(dto.categoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + dto.categoryId()));
            sc.setCategory(category);
        }

        return toDto(repo.save(sc));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public void softDelete(Long id) {
        StoreCategory sc = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StoreCategory not found: " + id));
        sc.setIsDeleted(true);
        repo.save(sc);
    }
}
