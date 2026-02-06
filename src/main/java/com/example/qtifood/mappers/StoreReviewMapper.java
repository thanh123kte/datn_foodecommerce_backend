package com.example.qtifood.mappers;

import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.qtifood.dtos.StoreReviews.CreateStoreReviewDto;
import com.example.qtifood.dtos.StoreReviews.ReviewImageDto;
import com.example.qtifood.dtos.StoreReviews.StoreReviewResponseDto;
import com.example.qtifood.entities.ReviewImage;
import com.example.qtifood.entities.StoreReview;

@Component
public class StoreReviewMapper {
    
    public StoreReview toEntity(CreateStoreReviewDto dto) {
        StoreReview storeReview = new StoreReview();
        storeReview.setRating(dto.getRating());
        storeReview.setComment(dto.getComment());
        return storeReview;
    }
    
    public StoreReviewResponseDto toDto(StoreReview storeReview) {
        StoreReviewResponseDto dto = new StoreReviewResponseDto();
        dto.setId(storeReview.getId());
        dto.setOrderId(storeReview.getOrder() != null ? storeReview.getOrder().getId() : null);
        dto.setStoreId(storeReview.getStore() != null ? storeReview.getStore().getId() : null);
        dto.setStoreName(storeReview.getStore() != null ? storeReview.getStore().getName() : null);
        dto.setCustomerId(storeReview.getCustomer() != null ? storeReview.getCustomer().getId() : null);
        dto.setCustomerName(storeReview.getCustomer() != null ? storeReview.getCustomer().getFullName() : null);
        dto.setCustomerAvatar(storeReview.getCustomer() != null ? storeReview.getCustomer().getAvatarUrl() : null);
        dto.setRating(storeReview.getRating());
        dto.setComment(storeReview.getComment());
        dto.setImages(storeReview.getImages() == null
                ? Collections.emptyList()
                : storeReview.getImages().stream().map(this::toImageDto).collect(Collectors.toList()));
        dto.setReply(storeReview.getReply());
        dto.setRepliedAt(storeReview.getRepliedAt());
        dto.setCreatedAt(storeReview.getCreatedAt());
        return dto;
    }
    
    private ReviewImageDto toImageDto(ReviewImage image) {
        ReviewImageDto dto = new ReviewImageDto();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setCreatedAt(image.getCreatedAt());
        return dto;
    }

}