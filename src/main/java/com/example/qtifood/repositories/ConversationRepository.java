package com.example.qtifood.repositories;

import com.example.qtifood.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    // Tìm conversation giữa customer và seller
    @Query("SELECT c FROM Conversation c " +
           "WHERE c.customer.id = :customerId AND c.seller.id = :sellerId")
    Optional<Conversation> findByCustomerIdAndSellerId(@Param("customerId") Long customerId, 
                                                      @Param("sellerId") Long sellerId);
    
    // Lấy tất cả conversations của customer
    @Query("SELECT c FROM Conversation c " +
           "LEFT JOIN FETCH c.customer " +
           "LEFT JOIN FETCH c.seller " +
           "WHERE c.customer.id = :customerId " +
           "ORDER BY c.createdAt DESC")
    List<Conversation> findByCustomerIdWithDetails(@Param("customerId") Long customerId);
    
    // Lấy tất cả conversations của seller
    @Query("SELECT c FROM Conversation c " +
           "LEFT JOIN FETCH c.customer " +
           "LEFT JOIN FETCH c.seller " +
           "WHERE c.seller.id = :sellerId " +
           "ORDER BY c.createdAt DESC")
    List<Conversation> findBySellerIdWithDetails(@Param("sellerId") Long sellerId);
    
    // Lấy conversations của user (có thể là customer hoặc seller)
    @Query("SELECT c FROM Conversation c " +
           "LEFT JOIN FETCH c.customer " +
           "LEFT JOIN FETCH c.seller " +
           "WHERE c.customer.id = :userId OR c.seller.id = :userId " +
           "ORDER BY c.createdAt DESC")
    List<Conversation> findByUserIdWithDetails(@Param("userId") Long userId);
    
    // Kiểm tra conversation tồn tại
    boolean existsByCustomerIdAndSellerId(Long customerId, Long sellerId);
    
    // Đếm conversations của customer
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.customer.id = :customerId")
    Long countByCustomerId(@Param("customerId") Long customerId);
    
    // Đếm conversations của seller
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.seller.id = :sellerId")
    Long countBySellerId(@Param("sellerId") Long sellerId);
}