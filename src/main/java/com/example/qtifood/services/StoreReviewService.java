package com.example.qtifood.services;

import com.example.qtifood.dtos.StoreReviews.CreateStoreReviewDto;
import com.example.qtifood.dtos.StoreReviews.StoreReviewResponseDto;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface StoreReviewService {
    StoreReviewResponseDto createStoreReview(CreateStoreReviewDto dto);

    void deleteStoreReview(Long id);
    StoreReviewResponseDto getStoreReviewById(Long id);
    List<StoreReviewResponseDto> getAllStoreReviews();
    List<StoreReviewResponseDto> getReviewsByStore(Long storeId);
    List<StoreReviewResponseDto> getReviewsByCustomer(String customerId);
    StoreReviewResponseDto getReviewByOrder(Long orderId);
    List<StoreReviewResponseDto> getReviewsByStoreAndRating(Long storeId, Integer rating);
    Double getAverageRatingByStore(Long storeId);
    Long getTotalReviewsByStore(Long storeId);
    StoreReviewResponseDto uploadImages(Long id, List<MultipartFile> imageFiles);
    StoreReviewResponseDto deleteImage(Long reviewId, Long imageId);
    StoreReviewResponseDto addReply(Long id, String reply);
    StoreReviewResponseDto deleteReply(Long id);
}