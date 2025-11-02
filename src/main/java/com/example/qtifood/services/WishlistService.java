package com.example.qtifood.services;

import com.example.qtifood.dtos.Wishlists.CreateWishlistDto;
import com.example.qtifood.dtos.Wishlists.WishlistResponseDto;

import java.util.List;

public interface WishlistService {
    
    WishlistResponseDto addToWishlist(Long customerId, CreateWishlistDto dto);
    
    void removeFromWishlist(Long customerId, Long storeId);
    
    void clearWishlist(Long customerId);
    
    List<WishlistResponseDto> getWishlist(Long customerId);
    
    Long getWishlistCount(Long customerId);
    
    boolean isStoreInWishlist(Long customerId, Long storeId);
    
    List<Long> getCustomersByStore(Long storeId);
}