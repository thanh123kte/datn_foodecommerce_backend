package com.example.qtifood.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.qtifood.entities.Banner;
import com.example.qtifood.enums.BannerStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    
    // Find banners by status
    List<Banner> findByStatus(BannerStatus status);
    
    // Find active banners (useful for public display)
    List<Banner> findByStatusOrderByCreatedAtDesc(BannerStatus status);
    
    // Find banners that should be active based on current date
    @Query("SELECT b FROM Banner b WHERE b.status = 'ACTIVE' " +
           "AND (b.startDate IS NULL OR b.startDate <= :now) " +
           "AND (b.endDate IS NULL OR b.endDate >= :now) " +
           "ORDER BY b.createdAt DESC")
    List<Banner> findCurrentlyActiveBanners(@Param("now") LocalDateTime now);
    
    // Find expired banners that need status update
    @Query("SELECT b FROM Banner b WHERE b.status = 'ACTIVE' " +
           "AND b.endDate IS NOT NULL AND b.endDate < :now")
    List<Banner> findExpiredBanners(@Param("now") LocalDateTime now);
}
