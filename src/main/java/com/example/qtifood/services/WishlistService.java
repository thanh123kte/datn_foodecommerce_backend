package com.example.qtifood.services;

import com.example.qtifood.dtos.Wishlists.CreateWishlistDto;
import com.example.qtifood.dtos.Wishlists.WishlistResponseDto;

import java.util.List;

public interface WishlistService {
    
    WishlistResponseDto addToWishlist(String customerId, CreateWishlistDto dto);
    
    void removeFromWishlist(String customerId, Long storeId);
    
    void clearWishlist(String customerId);
    
    List<WishlistResponseDto> getWishlist(String customerId);
    
    Long getWishlistCount(String customerId);
    
    boolean isStoreInWishlist(String customerId, Long storeId);
    
    List<String> getCustomersByStore(Long storeId);
}