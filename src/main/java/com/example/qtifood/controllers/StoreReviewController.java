package com.example.qtifood.controllers;

import com.example.qtifood.dtos.StoreReviews.CreateStoreReviewDto;
import com.example.qtifood.dtos.StoreReviews.StoreReviewResponseDto;
import com.example.qtifood.services.StoreReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/store-reviews")
@RequiredArgsConstructor
public class StoreReviewController {
    private final StoreReviewService storeReviewService;

    @PostMapping
    public ResponseEntity<StoreReviewResponseDto> createStoreReview(@Valid @RequestBody CreateStoreReviewDto dto) {
        return ResponseEntity.ok(storeReviewService.createStoreReview(dto));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStoreReview(@PathVariable Long id) {
        storeReviewService.deleteStoreReview(id);
        return ResponseEntity.ok("Store review deleted successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreReviewResponseDto> getStoreReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(storeReviewService.getStoreReviewById(id));
    }

    @GetMapping
    public ResponseEntity<List<StoreReviewResponseDto>> getAllStoreReviews() {
        return ResponseEntity.ok(storeReviewService.getAllStoreReviews());
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<StoreReviewResponseDto>> getReviewsByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(storeReviewService.getReviewsByStore(storeId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<StoreReviewResponseDto>> getReviewsByCustomer(@PathVariable String customerId) {
        return ResponseEntity.ok(storeReviewService.getReviewsByCustomer(customerId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<StoreReviewResponseDto> getReviewByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(storeReviewService.getReviewByOrder(orderId));
    }

    @GetMapping("/store/{storeId}/rating/{rating}")
    public ResponseEntity<List<StoreReviewResponseDto>> getReviewsByStoreAndRating(@PathVariable Long storeId, @PathVariable Integer rating) {
        return ResponseEntity.ok(storeReviewService.getReviewsByStoreAndRating(storeId, rating));
    }

    @GetMapping("/store/{storeId}/statistics")
    public ResponseEntity<Map<String, Object>> getStoreReviewStatistics(@PathVariable Long storeId) {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("averageRating", storeReviewService.getAverageRatingByStore(storeId));
        statistics.put("totalReviews", storeReviewService.getTotalReviewsByStore(storeId));
        return ResponseEntity.ok(statistics);
    }
}