package com.example.qtifood.repositories;

import com.example.qtifood.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    // Tìm tất cả cart items của một customer
    @Query("SELECT ci FROM CartItem ci " +
           "LEFT JOIN FETCH ci.product p " +
           "LEFT JOIN FETCH ci.store s " +
           "LEFT JOIN FETCH p.category " +
           "WHERE ci.customer.id = :customerId " +
           "ORDER BY ci.createdAt DESC")
    List<CartItem> findByCustomerIdWithDetails(@Param("customerId") Long customerId);
    
    // Tìm cart items của customer theo store
    @Query("SELECT ci FROM CartItem ci " +
           "LEFT JOIN FETCH ci.product p " +
           "LEFT JOIN FETCH ci.store s " +
           "WHERE ci.customer.id = :customerId AND ci.store.id = :storeId " +
           "ORDER BY ci.createdAt DESC")
    List<CartItem> findByCustomerIdAndStoreId(@Param("customerId") Long customerId, 
                                             @Param("storeId") Long storeId);
    
    // Tìm cart item specific của customer và product
    @Query("SELECT ci FROM CartItem ci " +
           "WHERE ci.customer.id = :customerId AND ci.product.id = :productId")
    Optional<CartItem> findByCustomerIdAndProductId(@Param("customerId") Long customerId, 
                                                   @Param("productId") Long productId);
    
    // Đếm số lượng items trong cart của customer
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.customer.id = :customerId")
    Long countByCustomerId(@Param("customerId") Long customerId);
    
    // Xóa tất cả cart items của customer
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.customer.id = :customerId")
    void deleteByCustomerId(@Param("customerId") Long customerId);
    
    // Xóa cart items của customer theo store
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.customer.id = :customerId AND ci.store.id = :storeId")
    void deleteByCustomerIdAndStoreId(@Param("customerId") Long customerId, 
                                     @Param("storeId") Long storeId);
    
    // Kiểm tra xem product có trong cart của customer không
    boolean existsByCustomerIdAndProductId(Long customerId, Long productId);
    
    // Lấy danh sách store ids mà customer có cart items
    @Query("SELECT DISTINCT ci.store.id FROM CartItem ci WHERE ci.customer.id = :customerId")
    List<Long> findDistinctStoreIdsByCustomerId(@Param("customerId") Long customerId);
}