package com.example.qtifood.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.qtifood.entities.StoreReview;

public interface StoreReviewRepository extends JpaRepository<StoreReview, Long> {
    List<StoreReview> findByStoreId(Long storeId);
    List<StoreReview> findByCustomerId(String customerId);
    Optional<StoreReview> findByOrderId(Long orderId);
    List<StoreReview> findByStoreIdOrderByCreatedAtDesc(Long storeId);
    List<StoreReview> findByStoreIdAndRating(Long storeId, Integer rating);
    
    @Query("SELECT AVG(sr.rating) FROM StoreReview sr WHERE sr.store.id = :storeId")
    Double getAverageRatingByStoreId(@Param("storeId") Long storeId);
    
    @Query("SELECT COUNT(sr) FROM StoreReview sr WHERE sr.store.id = :storeId")
    Long getTotalReviewsByStoreId(@Param("storeId") Long storeId);
    
    boolean existsByOrderId(Long orderId);
}