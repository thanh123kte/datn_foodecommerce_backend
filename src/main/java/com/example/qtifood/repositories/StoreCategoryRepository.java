// src/main/java/com/example/qtifood/repositories/StoreCategoryRepository.java
package com.example.qtifood.repositories;

import com.example.qtifood.entities.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Long> {
    List<StoreCategory> findAllByIsDeletedFalse();
    List<StoreCategory> findAllByStoreId(Long storeId);
    List<StoreCategory> findAllByCategory_Id(Long categoryId);
    List<StoreCategory> findAllByStore_Id(Long storeId);
    List<StoreCategory> findAllByStore_IdAndIsDeletedFalse(Long storeId);
    List<StoreCategory> findAllByCategory_IdAndIsDeletedFalse(Long categoryId);
}
