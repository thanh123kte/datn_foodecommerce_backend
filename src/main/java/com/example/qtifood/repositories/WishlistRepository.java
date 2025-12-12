package com.example.qtifood.repositories;

import com.example.qtifood.entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    
    // Tìm tất cả wishlist của một customer
    @Query("SELECT w FROM Wishlist w " +
           "LEFT JOIN FETCH w.store s " +
           "WHERE w.customer.id = :customerId " +
           "ORDER BY w.createdAt DESC")
    List<Wishlist> findByCustomerIdWithStoreDetails(@Param("customerId") String customerId);
    
    // Tìm wishlist specific của customer và store
    @Query("SELECT w FROM Wishlist w " +
           "WHERE w.customer.id = :customerId AND w.store.id = :storeId")
    Optional<Wishlist> findByCustomerIdAndStoreId(@Param("customerId") String customerId, 
                                                 @Param("storeId") Long storeId);
    
    // Đếm số lượng stores trong wishlist của customer
    @Query("SELECT COUNT(w) FROM Wishlist w WHERE w.customer.id = :customerId")
    Long countByCustomerId(@Param("customerId") String customerId);
    
    // Xóa tất cả wishlist của customer
    @Modifying
    @Query("DELETE FROM Wishlist w WHERE w.customer.id = :customerId")
    void deleteByCustomerId(@Param("customerId") String customerId);
    
    // Kiểm tra xem store có trong wishlist của customer không
    boolean existsByCustomerIdAndStoreId(String customerId, Long storeId);
    
    // Lấy danh sách customer ids đã thêm store vào wishlist
    @Query("SELECT DISTINCT w.customer.id FROM Wishlist w WHERE w.store.id = :storeId")
    List<String> findCustomerIdsByStoreId(@Param("storeId") Long storeId);
    
    // Xóa wishlist theo customer và store
    @Modifying
    @Query("DELETE FROM Wishlist w WHERE w.customer.id = :customerId AND w.store.id = :storeId")
    void deleteByCustomerIdAndStoreId(@Param("customerId") String customerId, 
                                     @Param("storeId") Long storeId);
}