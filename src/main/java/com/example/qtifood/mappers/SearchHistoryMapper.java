package com.example.qtifood.mappers;

import com.example.qtifood.dtos.SearchHistory.SearchHistoryResponseDto;
import com.example.qtifood.entities.SearchHistory;

import java.util.List;
import java.util.stream.Collectors;

public class SearchHistoryMapper {

    public static SearchHistoryResponseDto toDto(SearchHistory searchHistory) {
        if (searchHistory == null) {
            return null;
        }

        return SearchHistoryResponseDto.builder()
                .id(searchHistory.getId())
                .userId(searchHistory.getUser().getId())
                .keyword(searchHistory.getKeyword())
                .createdAt(searchHistory.getCreatedAt())
                .build();
    }

    public static List<SearchHistoryResponseDto> toDtoList(List<SearchHistory> searchHistories) {
        return searchHistories.stream()
                .map(SearchHistoryMapper::toDto)
                .collect(Collectors.toList());
    }
}