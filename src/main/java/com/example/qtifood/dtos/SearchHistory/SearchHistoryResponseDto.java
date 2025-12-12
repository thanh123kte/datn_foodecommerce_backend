package com.example.qtifood.dtos.SearchHistory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistoryResponseDto {
    
    private Long id;
    private String userId;
    private String keyword;
    private LocalDateTime createdAt;
}