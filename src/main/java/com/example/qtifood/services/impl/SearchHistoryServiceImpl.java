package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.SearchHistory.CreateSearchHistoryDto;
import com.example.qtifood.dtos.SearchHistory.SearchHistoryResponseDto;
import com.example.qtifood.entities.SearchHistory;
import com.example.qtifood.entities.User;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.mappers.SearchHistoryMapper;
import com.example.qtifood.repositories.SearchHistoryRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.services.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SearchHistoryServiceImpl implements SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;

    @Override
    public SearchHistoryResponseDto saveSearchHistory(String userId, CreateSearchHistoryDto dto) {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Tránh lưu keyword trùng lặp gần đây (có thể check trong vòng 1 giờ)
        String keyword = dto.getKeyword().trim().toLowerCase();
        
        // Tạo search history mới
        SearchHistory searchHistory = SearchHistory.builder()
                .user(user)
                .keyword(keyword)
                .build();

        searchHistory = searchHistoryRepository.save(searchHistory);
        log.info("Saved search history for user {} with keyword: {}", userId, keyword);
        
        return SearchHistoryMapper.toDto(searchHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SearchHistoryResponseDto> getSearchHistory(String userId, Pageable pageable) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        Page<SearchHistory> searchHistories = searchHistoryRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        return searchHistories.map(SearchHistoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SearchHistoryResponseDto> getSearchHistory(String userId) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<SearchHistory> searchHistories = searchHistoryRepository
                .findByUserIdOrderByCreatedAtDesc(userId);
        
        return SearchHistoryMapper.toDtoList(searchHistories);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopKeywordsByUser(String userId, int limit) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = searchHistoryRepository.findTopKeywordsByUserId(userId, pageable);
        
        return results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("keyword", result[0]);
                    map.put("count", result[1]);
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getRecentKeywordsByUser(String userId, int limit) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = searchHistoryRepository.findRecentKeywordsByUserId(userId, pageable);
        
        return results.stream()
                .map(result -> (String) result[0]) // Lấy keyword từ Object[]
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopKeywords(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = searchHistoryRepository.findTopKeywords(pageable);
        
        return results.stream()
                .map(result -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("keyword", result[0]);
                    map.put("count", result[1]);
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getSearchCount(String userId) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return searchHistoryRepository.countByUserId(userId);
    }

    @Override
    public void clearSearchHistory(String userId) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        searchHistoryRepository.deleteByUserId(userId);
        log.info("Cleared search history for user: {}", userId);
    }

    @Override
    public void deleteSearchKeyword(String userId, String keyword) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        searchHistoryRepository.deleteByUserIdAndKeyword(userId, keyword.trim().toLowerCase());
        log.info("Deleted search keyword '{}' for user: {}", keyword, userId);
    }

    @Override
    public void cleanupOldSearchHistory(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        searchHistoryRepository.deleteByCreatedAtBefore(cutoffDate);
        log.info("Cleaned up search history older than {} days", daysOld);
    }
}