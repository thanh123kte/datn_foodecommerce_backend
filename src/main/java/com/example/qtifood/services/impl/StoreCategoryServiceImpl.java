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
                sc.getCreatedAt(),
                sc.getUpdatedAt()
        );
    }

    @Override
    public StoreCategoryResponseDto create(CreateStoreCategoryDto dto) {
        // bắt buộc có storeId
        Store store = storeRepo.findById(dto.storeId())
                .orElseThrow(() -> new IllegalArgumentException("Store not found: " + dto.storeId()));

        Categories parent = null;
        if (dto.parentCategoryId() != null) {
            parent = categoriesRepo.findById(dto.parentCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found: " + dto.parentCategoryId()));
        }

        StoreCategory sc = StoreCategory.builder()
                .store(store)
                .name(dto.name())
                .description(dto.description())
                .category(parent)
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
    public List<StoreCategoryResponseDto> listByStore(Long storeId) {
        return repo.findAllByStore_Id(storeId)   // <— đổi sang field store
                  .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreCategoryResponseDto> listByParentCategory(Long parentCategoryId) {
        return repo.findAllByCategory_Id(parentCategoryId)
                  .stream().map(this::toDto).toList();
    }

    @Override
    public StoreCategoryResponseDto update(Long id, UpdateStoreCategoryDto dto) {
        StoreCategory sc = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("StoreCategory not found: " + id));

        if (dto.name() != null)        sc.setName(dto.name());
        if (dto.description() != null) sc.setDescription(dto.description());

        // ⚠️ Chỉ đổi parent khi client cung cấp parentCategoryId (không tự động xóa khi null)
        if (dto.parentCategoryId() != null) {
            Categories parent = categoriesRepo.findById(dto.parentCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found: " + dto.parentCategoryId()));
            sc.setCategory(parent);
        }

        return toDto(repo.save(sc));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
