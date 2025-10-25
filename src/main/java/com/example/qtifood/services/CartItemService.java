package com.example.qtifood.services;

import com.example.qtifood.dtos.CartItems.CreateCartItemDto;
import com.example.qtifood.dtos.CartItems.UpdateCartItemDto;
import com.example.qtifood.dtos.CartItems.CartItemResponseDto;
import com.example.qtifood.dtos.CartItems.CartSummaryDto;

import java.util.List;

public interface CartItemService {
    
    CartItemResponseDto addToCart(Long customerId, CreateCartItemDto dto);
    
    CartItemResponseDto updateCartItem(Long customerId, Long cartItemId, UpdateCartItemDto dto);
    
    void removeFromCart(Long customerId, Long cartItemId);
    
    void clearCart(Long customerId);
    
    void clearCartByStore(Long customerId, Long storeId);
    
    List<CartItemResponseDto> getCartItems(Long customerId);
    
    List<CartItemResponseDto> getCartItemsByStore(Long customerId, Long storeId);
    
    List<CartSummaryDto> getCartSummary(Long customerId);
    
    CartItemResponseDto getCartItem(Long customerId, Long cartItemId);
    
    Long getCartItemsCount(Long customerId);
    
    boolean isProductInCart(Long customerId, Long productId);
}