package com.example.qtifood.services;

import com.example.qtifood.dtos.CartItems.CreateCartItemDto;
import com.example.qtifood.dtos.CartItems.UpdateCartItemDto;
import com.example.qtifood.dtos.CartItems.CartItemResponseDto;
import com.example.qtifood.dtos.CartItems.CartSummaryDto;

import java.util.List;

public interface CartItemService {
    
    CartItemResponseDto addToCart(String customerId, CreateCartItemDto dto);
    
    CartItemResponseDto updateCartItem(String customerId, Long cartItemId, UpdateCartItemDto dto);
    
    void removeFromCart(String customerId, Long cartItemId);
    
    void clearCart(String customerId);
    
    void clearCartByStore(String customerId, Long storeId);
    
    List<CartItemResponseDto> getCartItems(String customerId);
    
    List<CartItemResponseDto> getCartItemsByStore(String customerId, Long storeId);
    
    List<CartSummaryDto> getCartSummary(String customerId);
    
    CartItemResponseDto getCartItem(String customerId, Long cartItemId);
    
    Long getCartItemsCount(String customerId);
    
    boolean isProductInCart(String customerId, Long productId);
}