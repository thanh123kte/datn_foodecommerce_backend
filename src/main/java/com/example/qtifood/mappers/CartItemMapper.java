package com.example.qtifood.mappers;

import com.example.qtifood.dtos.CartItems.CartItemResponseDto;
import com.example.qtifood.dtos.CartItems.CartSummaryDto;
import com.example.qtifood.entities.CartItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CartItemMapper {

    public static CartItemResponseDto toDto(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        BigDecimal productPrice = cartItem.getProduct().getDiscountPrice() != null ? 
                                 cartItem.getProduct().getDiscountPrice() : 
                                 cartItem.getProduct().getPrice();
        
        BigDecimal totalPrice = productPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponseDto.builder()
                .id(cartItem.getId())
                .customerId(cartItem.getCustomer().getId())
                .product(ProductMapper.toDto(cartItem.getProduct()))
                .store(StoreMapper.toDto(cartItem.getStore()))
                .quantity(cartItem.getQuantity())
                .note(cartItem.getNote())
                .totalPrice(totalPrice)
                .createdAt(cartItem.getCreatedAt())
                .updatedAt(cartItem.getUpdatedAt())
                .build();
    }

    public static List<CartItemResponseDto> toDtoList(List<CartItem> cartItems) {
        if (cartItems == null) {
            return null;
        }
        return cartItems.stream()
                .map(CartItemMapper::toDto)
                .collect(Collectors.toList());
    }

    public static List<CartSummaryDto> toCartSummaryList(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return List.of();
        }

        // Group cart items by store
        Map<Long, List<CartItem>> itemsByStore = cartItems.stream()
                .collect(Collectors.groupingBy(item -> item.getStore().getId()));

        return itemsByStore.entrySet().stream()
                .map(entry -> {
                    List<CartItem> storeItems = entry.getValue();
                    CartItem firstItem = storeItems.get(0);
                    
                    List<CartItemResponseDto> itemDtos = toDtoList(storeItems);
                    
                    BigDecimal totalAmount = itemDtos.stream()
                            .map(CartItemResponseDto::getTotalPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    Integer totalItems = storeItems.stream()
                            .mapToInt(CartItem::getQuantity)
                            .sum();

                    return CartSummaryDto.builder()
                            .storeId(firstItem.getStore().getId())
                            .storeName(firstItem.getStore().getName())
                            .items(itemDtos)
                            .totalItems(totalItems)
                            .totalAmount(totalAmount)
                            .build();
                })
                .collect(Collectors.toList());
    }
}