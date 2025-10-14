package com.example.qtifood.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.qtifood.entities.Product;
import com.example.qtifood.enums.ProductStatus;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByStoreId(Long storeId);
    
    List<Product> findByCategoryId(Long categoryId);
    
    List<Product> findByStatus(ProductStatus status);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByStoreIdAndStatus(Long storeId, ProductStatus status);
    
    boolean existsByStoreIdAndNameIgnoreCase(Long storeId, String name);
    
    boolean existsByStoreIdAndNameIgnoreCaseAndIdNot(Long storeId, String name, Long id);
    
    @Query("SELECT p FROM Product p WHERE p.store.id = :storeId AND p.name LIKE %:name%")
    List<Product> findByStoreIdAndNameContaining(@Param("storeId") Long storeId, 
                                               @Param("name") String name);
    
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.status = :status")
    List<Product> findByCategoryIdAndStatus(@Param("categoryId") Long categoryId, 
                                          @Param("status") ProductStatus status);
}