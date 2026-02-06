package com.example.qtifood.repositories;

import com.example.qtifood.entities.SearchHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    
    // Tìm lịch sử tìm kiếm của user
    @Query("SELECT sh FROM SearchHistory sh " +
           "LEFT JOIN FETCH sh.user " +
           "WHERE sh.user.id = :userId " +
           "ORDER BY sh.createdAt DESC")
    Page<SearchHistory> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId, Pageable pageable);
    
    // Tìm lịch sử tìm kiếm của user (không phân trang)
    @Query("SELECT sh FROM SearchHistory sh " +
           "WHERE sh.user.id = :userId " +
           "ORDER BY sh.createdAt DESC")
    List<SearchHistory> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);
    
    // Lấy top keywords phổ biến của user (theo số lần tìm kiếm)
    @Query("SELECT sh.keyword, COUNT(sh.keyword) as count FROM SearchHistory sh " +
           "WHERE sh.user.id = :userId " +
           "GROUP BY sh.keyword " +
           "ORDER BY count DESC")
    List<Object[]> findTopKeywordsByUserId(@Param("userId") String userId, Pageable pageable);
    
    // Lấy keywords mới nhất của user (theo thời gian) - Fixed SQL
    @Query("SELECT sh.keyword, MAX(sh.createdAt) as lastUsed FROM SearchHistory sh " +
           "WHERE sh.user.id = :userId " +
           "GROUP BY sh.keyword " +
           "ORDER BY lastUsed DESC")
    List<Object[]> findRecentKeywordsByUserId(@Param("userId") String userId, Pageable pageable);
    
    // Lấy top keywords phổ biến của tất cả user
    @Query("SELECT sh.keyword, COUNT(sh.keyword) as count FROM SearchHistory sh " +
           "GROUP BY sh.keyword " +
           "ORDER BY count DESC")
    List<Object[]> findTopKeywords(Pageable pageable);
    
    // Đếm số lượng tìm kiếm của user
    @Query("SELECT COUNT(sh) FROM SearchHistory sh WHERE sh.user.id = :userId")
    Long countByUserId(@Param("userId") String userId);
    
    // Xóa lịch sử tìm kiếm cũ (trước ngày nào đó)
    @Modifying
    @Query("DELETE FROM SearchHistory sh WHERE sh.createdAt < :before")
    void deleteByCreatedAtBefore(@Param("before") LocalDateTime before);
    
    // Xóa tất cả lịch sử tìm kiếm của user
    @Modifying
    @Query("DELETE FROM SearchHistory sh WHERE sh.user.id = :userId")
    void deleteByUserId(@Param("userId") String userId);
    
    // Xóa keyword cụ thể của user
    @Modifying
    @Query("DELETE FROM SearchHistory sh WHERE sh.user.id = :userId AND sh.keyword = :keyword")
    void deleteByUserIdAndKeyword(@Param("userId") String userId, @Param("keyword") String keyword);
    
    // Kiểm tra xem user đã tìm kiếm keyword này chưa
    boolean existsByUserIdAndKeyword(String userId, String keyword);
}