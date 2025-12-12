package com.example.qtifood.controllers;

import com.example.qtifood.dtos.SearchHistory.CreateSearchHistoryDto;
import com.example.qtifood.dtos.SearchHistory.SearchHistoryResponseDto;
import com.example.qtifood.services.SearchHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search-history")
@RequiredArgsConstructor
@Validated
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    // Lưu lịch sử tìm kiếm
    @PostMapping("/user/{userId}")
    public ResponseEntity<SearchHistoryResponseDto> saveSearchHistory(
            @PathVariable String userId,
            @Valid @RequestBody CreateSearchHistoryDto dto) {
        SearchHistoryResponseDto searchHistory = searchHistoryService.saveSearchHistory(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(searchHistory);
    }

    // Lấy lịch sử tìm kiếm với phân trang
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<SearchHistoryResponseDto>> getSearchHistory(
            @PathVariable String userId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<SearchHistoryResponseDto> searchHistory = searchHistoryService.getSearchHistory(userId, pageable);
        return ResponseEntity.ok(searchHistory);
    }

    // Lấy keywords mới nhất của user (thay vì top keywords)
    @GetMapping("/user/{userId}/top-keywords")
    public ResponseEntity<List<String>> getTopKeywordsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<String> recentKeywords = searchHistoryService.getRecentKeywordsByUser(userId, limit);
        return ResponseEntity.ok(recentKeywords);
    }

    // Lấy top keywords phổ biến (theo số lần tìm kiếm) 
    @GetMapping("/user/{userId}/frequent-keywords")
    public ResponseEntity<List<Map<String, Object>>> getFrequentKeywordsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> topKeywords = searchHistoryService.getTopKeywordsByUser(userId, limit);
        return ResponseEntity.ok(topKeywords);
    }

    // Xóa tất cả lịch sử tìm kiếm của user
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Map<String, String>> clearSearchHistory(@PathVariable String userId) {
        searchHistoryService.clearSearchHistory(userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Search history cleared successfully");
        return ResponseEntity.ok(response);
    }

    // Xóa keyword cụ thể
    @DeleteMapping("/user/{userId}/keyword")
    public ResponseEntity<Map<String, String>> deleteSearchKeyword(
            @PathVariable String userId,
            @RequestParam String keyword) {
        searchHistoryService.deleteSearchKeyword(userId, keyword);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Search keyword deleted successfully");
        return ResponseEntity.ok(response);
    }
}