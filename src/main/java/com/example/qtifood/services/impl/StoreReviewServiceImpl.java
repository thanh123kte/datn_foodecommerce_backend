package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.StoreReviews.CreateStoreReviewDto;
import com.example.qtifood.dtos.StoreReviews.StoreReviewResponseDto;
import com.example.qtifood.entities.StoreReview;
import com.example.qtifood.entities.Order;
import com.example.qtifood.entities.Store;
import com.example.qtifood.entities.User;
import com.example.qtifood.enums.OrderStatus;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.exceptions.EntityDuplicateException;
import com.example.qtifood.mappers.StoreReviewMapper;
import com.example.qtifood.repositories.StoreReviewRepository;
import com.example.qtifood.repositories.OrderRepository;
import com.example.qtifood.repositories.StoreRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.services.StoreReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        
        return storeReviewMapper.toDto(storeReviewRepository.save(storeReview));
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
}