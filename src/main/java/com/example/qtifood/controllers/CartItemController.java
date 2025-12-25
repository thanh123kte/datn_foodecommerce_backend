package com.example.qtifood.controllers;

import com.example.qtifood.dtos.CartItems.CreateCartItemDto;
import com.example.qtifood.dtos.CartItems.UpdateCartItemDto;
import com.example.qtifood.dtos.CartItems.CartItemResponseDto;
import com.example.qtifood.dtos.CartItems.CartSummaryDto;
import com.example.qtifood.services.CartItemService;
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
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Validated
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping("/{customerId}")
    public ResponseEntity<CartItemResponseDto> addToCart(
            @PathVariable String customerId,
            @Valid @RequestBody CreateCartItemDto dto) {
        CartItemResponseDto cartItem = cartItemService.addToCart(customerId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
    }

    @PutMapping("/{customerId}/items/{cartItemId}")
    public ResponseEntity<CartItemResponseDto> updateCartItem(
            @PathVariable String customerId,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemDto dto) {
        CartItemResponseDto cartItem = cartItemService.updateCartItem(customerId, cartItemId, dto);
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("/{customerId}/items/{cartItemId}")
    public ResponseEntity<Map<String, String>> removeFromCart(
            @PathVariable String customerId,
            @PathVariable Long cartItemId) {
        cartItemService.removeFromCart(customerId, cartItemId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Item removed from cart successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Map<String, String>> clearCart(@PathVariable String customerId) {
        cartItemService.clearCart(customerId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Cart cleared successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{customerId}/store/{storeId}")
    public ResponseEntity<Map<String, String>> clearCartByStore(
            @PathVariable String customerId,
            @PathVariable Long storeId) {
        cartItemService.clearCartByStore(customerId, storeId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Store items removed from cart successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<CartItemResponseDto>> getCartItems(@PathVariable String customerId) {
        List<CartItemResponseDto> cartItems = cartItemService.getCartItems(customerId);
        return ResponseEntity.ok(cartItems);
    }

    @GetMapping("/{customerId}/store/{storeId}")
    public ResponseEntity<List<CartItemResponseDto>> getCartItemsByStore(
            @PathVariable String customerId,
            @PathVariable Long storeId) {
        List<CartItemResponseDto> cartItems = cartItemService.getCartItemsByStore(customerId, storeId);
        return ResponseEntity.ok(cartItems);
    }

    @GetMapping("/{customerId}/summary")
    public ResponseEntity<List<CartSummaryDto>> getCartSummary(@PathVariable String customerId) {
        List<CartSummaryDto> cartSummary = cartItemService.getCartSummary(customerId);
        return ResponseEntity.ok(cartSummary);
    }

    @GetMapping("/{customerId}/items/{cartItemId}")
    public ResponseEntity<CartItemResponseDto> getCartItem(
            @PathVariable String customerId,
            @PathVariable Long cartItemId) {
        CartItemResponseDto cartItem = cartItemService.getCartItem(customerId, cartItemId);
        return ResponseEntity.ok(cartItem);
    }

    @GetMapping("/{customerId}/count")
    public ResponseEntity<Map<String, Long>> getCartItemsCount(@PathVariable String customerId) {
        Long count = cartItemService.getCartItemsCount(customerId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> isProductInCart(
            @PathVariable String customerId,
            @PathVariable Long productId) {
        boolean inCart = cartItemService.isProductInCart(customerId, productId);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("inCart", inCart);
        return ResponseEntity.ok(response);
    }
}