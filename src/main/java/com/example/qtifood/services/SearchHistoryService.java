package com.example.qtifood.services;

import com.example.qtifood.dtos.SearchHistory.CreateSearchHistoryDto;
import com.example.qtifood.dtos.SearchHistory.SearchHistoryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface SearchHistoryService {
    
    // Lưu lịch sử tìm kiếm
    SearchHistoryResponseDto saveSearchHistory(String userId, CreateSearchHistoryDto dto);
    
    // Lấy lịch sử tìm kiếm của user với phân trang
    Page<SearchHistoryResponseDto> getSearchHistory(String userId, Pageable pageable);
    
    // Lấy lịch sử tìm kiếm của user (không phân trang)
    List<SearchHistoryResponseDto> getSearchHistory(String userId);
    
    // Lấy top keywords phổ biến của user (theo số lần tìm kiếm)
    List<Map<String, Object>> getTopKeywordsByUser(String userId, int limit);
    
    // Lấy keywords mới nhất của user (theo thời gian)
    List<String> getRecentKeywordsByUser(String userId, int limit);
    
    // Lấy top keywords phổ biến của tất cả user
    List<Map<String, Object>> getTopKeywords(int limit);
    
    // Đếm số lượng tìm kiếm của user
    Long getSearchCount(String userId);
    
    // Xóa tất cả lịch sử tìm kiếm của user
    void clearSearchHistory(String userId);
    
    // Xóa keyword cụ thể của user
    void deleteSearchKeyword(String userId, String keyword);
    
    // Xóa lịch sử tìm kiếm cũ (cleanup job)
    void cleanupOldSearchHistory(int daysOld);
}