package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.StoreReviews.CreateStoreReviewDto;
import com.example.qtifood.dtos.StoreReviews.StoreReviewResponseDto;
import com.example.qtifood.entities.ReviewImage;
import com.example.qtifood.entities.StoreReview;
import com.example.qtifood.entities.Order;
import com.example.qtifood.entities.Store;
import com.example.qtifood.entities.User;
import com.example.qtifood.enums.OrderStatus;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.exceptions.EntityDuplicateException;
import com.example.qtifood.mappers.StoreReviewMapper;
import com.example.qtifood.repositories.ReviewImageRepository;
import com.example.qtifood.repositories.StoreReviewRepository;
import com.example.qtifood.repositories.OrderRepository;
import com.example.qtifood.repositories.StoreRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.services.StoreReviewService;
import com.example.qtifood.services.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreReviewServiceImpl implements StoreReviewService {
    private final StoreReviewRepository storeReviewRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreReviewMapper storeReviewMapper;
    private final FileUploadService fileUploadService;
    private final ReviewImageRepository reviewImageRepository;

    @Override
    @Transactional
    public StoreReviewResponseDto createStoreReview(CreateStoreReviewDto dto) {
        // Check if review for this order already exists
        if (storeReviewRepository.existsByOrderId(dto.getOrderId())) {
            throw new EntityDuplicateException("Review for this order already exists");
        }
        
        StoreReview storeReview = storeReviewMapper.toEntity(dto);
        
        // Set order
        Order order = orderRepository.findById(dto.getOrderId())
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        storeReview.setOrder(order);
        
        // Set store
        Store store = storeRepository.findById(dto.getStoreId())
            .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        storeReview.setStore(store);
        
        // Set customer
        User customer = userRepository.findById(dto.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        storeReview.setCustomer(customer);
        
        // Validation: Ensure order belongs to the customer
        if (!order.getCustomer().getId().equals(dto.getCustomerId())) {
            throw new IllegalArgumentException("Order does not belong to the specified customer");
        }
        
        // Validation: Ensure order is from the specified store
        if (!order.getStore().getId().equals(dto.getStoreId())) {
            throw new IllegalArgumentException("Order is not from the specified store");
        }
        
        // Validation: Order must be DELIVERED to be reviewed
        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("Only delivered orders can be reviewed");
        }
        
        StoreReview saved = storeReviewRepository.save(storeReview);

        // Mark order as reviewed and set rating flag
        order.setOrderStatus(OrderStatus.REVIEWED);
        order.setRatingStatus(Boolean.TRUE);
        orderRepository.save(order);

        return storeReviewMapper.toDto(saved);
    }



    @Override
    @Transactional
    public void deleteStoreReview(Long id) {
        StoreReview storeReview = storeReviewRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Store review not found"));
        storeReviewRepository.delete(storeReview);
    }

    @Override
    @Transactional(readOnly = true)
    public StoreReviewResponseDto getStoreReviewById(Long id) {
        return storeReviewMapper.toDto(storeReviewRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Store review not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreReviewResponseDto> getAllStoreReviews() {
        return storeReviewRepository.findAll().stream()
            .map(storeReviewMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreReviewResponseDto> getReviewsByStore(Long storeId) {
        return storeReviewRepository.findByStoreIdOrderByCreatedAtDesc(storeId).stream()
            .map(storeReviewMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreReviewResponseDto> getReviewsByCustomer(String customerId) {
        return storeReviewRepository.findByCustomerId(customerId).stream()
            .map(storeReviewMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StoreReviewResponseDto getReviewByOrder(Long orderId) {
        return storeReviewMapper.toDto(storeReviewRepository.findByOrderId(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found for this order")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreReviewResponseDto> getReviewsByStoreAndRating(Long storeId, Integer rating) {
        return storeReviewRepository.findByStoreIdAndRating(storeId, rating).stream()
            .map(storeReviewMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingByStore(Long storeId) {
        Double average = storeReviewRepository.getAverageRatingByStoreId(storeId);
        return average != null ? average : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalReviewsByStore(Long storeId) {
        return storeReviewRepository.getTotalReviewsByStoreId(storeId);
    }

    @Override
    @Transactional
    public StoreReviewResponseDto uploadImages(Long id, List<MultipartFile> imageFiles) {
        if (imageFiles == null || imageFiles.isEmpty()) {
            throw new IllegalArgumentException("At least one image file is required");
        }

        StoreReview review = storeReviewRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Store review not found: " + id));

        for (MultipartFile imageFile : imageFiles) {
            String newImagePath = fileUploadService.uploadFile(imageFile, "reviews", id.toString());
            ReviewImage image = ReviewImage.builder()
                    .review(review)
                    .imageUrl(newImagePath)
                    .build();
            review.getImages().add(image);
        }

        return storeReviewMapper.toDto(storeReviewRepository.save(review));
    }

    @Override
    @Transactional
    public StoreReviewResponseDto deleteImage(Long reviewId, Long imageId) {
        StoreReview review = storeReviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Store review not found: " + reviewId));

        ReviewImage image = reviewImageRepository.findById(imageId)
            .orElseThrow(() -> new ResourceNotFoundException("Review image not found: " + imageId));

        if (!image.getReview().getId().equals(reviewId)) {
            throw new IllegalArgumentException("Image does not belong to the specified review");
        }

        // Remove from review collection (orphanRemoval handles delete)
        review.getImages().removeIf(img -> img.getId().equals(imageId));

        // Delete physical file if not external URL
        if (image.getImageUrl() != null && !image.getImageUrl().trim().isEmpty()) {
            if (!image.getImageUrl().startsWith("http://") && !image.getImageUrl().startsWith("https://")) {
                try {
                    fileUploadService.deleteFile(image.getImageUrl());
                } catch (Exception e) {
                    System.err.println("Failed to delete review image: " + e.getMessage());
                }
            }
        }

        storeReviewRepository.save(review);
        return storeReviewMapper.toDto(review);
    }

    @Override
    @Transactional
    public StoreReviewResponseDto addReply(Long id, String reply) {
        StoreReview review = storeReviewRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Store review not found: " + id));
        
        if (reply == null || reply.trim().isEmpty()) {
            throw new IllegalArgumentException("Reply cannot be empty");
        }
        
        review.setReply(reply.trim());
        review.setRepliedAt(java.time.LocalDateTime.now());
        
        return storeReviewMapper.toDto(storeReviewRepository.save(review));
    }

    @Override
    @Transactional
    public StoreReviewResponseDto deleteReply(Long id) {
        StoreReview review = storeReviewRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Store review not found: " + id));
        
        review.setReply(null);
        review.setRepliedAt(null);
        
        return storeReviewMapper.toDto(storeReviewRepository.save(review));
    }
}