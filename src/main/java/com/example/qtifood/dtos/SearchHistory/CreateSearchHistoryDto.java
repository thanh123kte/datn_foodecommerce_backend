package com.example.qtifood.dtos.SearchHistory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSearchHistoryDto {
    
    @NotBlank(message = "Keyword is required")
    @Size(max = 255, message = "Keyword must not exceed 255 characters")
    private String keyword;
}