package com.example.qtifood.controllers;

import com.example.qtifood.dtos.Wishlists.CreateWishlistDto;
import com.example.qtifood.dtos.Wishlists.WishlistResponseDto;
import com.example.qtifood.services.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Validated
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/{customerId}")
    public ResponseEntity<WishlistResponseDto> addToWishlist(
            @PathVariable String customerId,
            @Valid @RequestBody CreateWishlistDto dto) {
        WishlistResponseDto wishlist = wishlistService.addToWishlist(customerId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(wishlist);
    }

    @DeleteMapping("/{customerId}/store/{storeId}")
    public ResponseEntity<Map<String, String>> removeFromWishlist(
            @PathVariable String customerId,
            @PathVariable Long storeId) {
        wishlistService.removeFromWishlist(customerId, storeId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Store removed from wishlist successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Map<String, String>> clearWishlist(@PathVariable String customerId) {
        wishlistService.clearWishlist(customerId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Wishlist cleared successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<WishlistResponseDto>> getWishlist(@PathVariable String customerId) {
        List<WishlistResponseDto> wishlist = wishlistService.getWishlist(customerId);
        return ResponseEntity.ok(wishlist);
    }

    @GetMapping("/{customerId}/count")
    public ResponseEntity<Map<String, Long>> getWishlistCount(@PathVariable String customerId) {
        Long count = wishlistService.getWishlistCount(customerId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}/check/{storeId}")
    public ResponseEntity<Map<String, Boolean>> isStoreInWishlist(
            @PathVariable String customerId,
            @PathVariable Long storeId) {
        boolean inWishlist = wishlistService.isStoreInWishlist(customerId, storeId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("inWishlist", inWishlist);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/store/{storeId}/customers")
    public ResponseEntity<Map<String, Object>> getCustomersByStore(@PathVariable Long storeId) {
        List<String> customerIds = wishlistService.getCustomersByStore(storeId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("customerIds", customerIds);
        response.put("count", customerIds.size());
        return ResponseEntity.ok(response);
    }
}