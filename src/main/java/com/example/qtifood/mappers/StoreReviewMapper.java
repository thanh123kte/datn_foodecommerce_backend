package com.example.qtifood.mappers;

import com.example.qtifood.dtos.StoreReviews.CreateStoreReviewDto;
import com.example.qtifood.dtos.StoreReviews.StoreReviewResponseDto;
import com.example.qtifood.entities.StoreReview;
import org.springframework.stereotype.Component;

@Component
public class StoreReviewMapper {
    
    public StoreReview toEntity(CreateStoreReviewDto dto) {
        StoreReview storeReview = new StoreReview();
        storeReview.setRating(dto.getRating());
        storeReview.setComment(dto.getComment());
        storeReview.setImageUrl(dto.getImageUrl());
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
        dto.setImageUrl(storeReview.getImageUrl());
        dto.setReply(storeReview.getReply());
        dto.setRepliedAt(storeReview.getRepliedAt());
        dto.setCreatedAt(storeReview.getCreatedAt());
        return dto;
    }
    

}