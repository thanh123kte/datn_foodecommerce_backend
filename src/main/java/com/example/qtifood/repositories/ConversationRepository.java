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
    Optional<Conversation> findByCustomerIdAndSellerId(@Param("customerId") String customerId, 
                                                      @Param("sellerId") String sellerId);
    
    // Lấy tất cả conversations của customer
    @Query("SELECT c FROM Conversation c " +
           "LEFT JOIN FETCH c.customer " +
           "LEFT JOIN FETCH c.seller " +
           "WHERE c.customer.id = :customerId " +
           "ORDER BY c.createdAt DESC")
    List<Conversation> findByCustomerIdWithDetails(@Param("customerId") String customerId);
    
    // Lấy tất cả conversations của seller
    @Query("SELECT c FROM Conversation c " +
           "LEFT JOIN FETCH c.customer " +
           "LEFT JOIN FETCH c.seller " +
           "WHERE c.seller.id = :sellerId " +
           "ORDER BY c.createdAt DESC")
    List<Conversation> findBySellerIdWithDetails(@Param("sellerId") String sellerId);
    
    // Lấy conversations của user (có thể là customer hoặc seller)
    @Query("SELECT c FROM Conversation c " +
           "LEFT JOIN FETCH c.customer " +
           "LEFT JOIN FETCH c.seller " +
           "WHERE c.customer.id = :userId OR c.seller.id = :userId " +
           "ORDER BY c.createdAt DESC")
    List<Conversation> findByUserIdWithDetails(@Param("userId") String userId);
    
    // Kiểm tra conversation tồn tại
    boolean existsByCustomerIdAndSellerId(String customerId, String sellerId);
    
    // Đếm conversations của customer
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.customer.id = :customerId")
    Long countByCustomerId(@Param("customerId") String customerId);
    
    // Đếm conversations của seller
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.seller.id = :sellerId")
    Long countBySellerId(@Param("sellerId") String sellerId);
}