package com.example.qtifood.dtos.StoreReviews;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class UpdateStoreReviewDto {
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;
    
    private String comment;
    private String imageUrl;
}