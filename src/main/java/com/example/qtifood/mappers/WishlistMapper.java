package com.example.qtifood.mappers;

import com.example.qtifood.dtos.Wishlists.WishlistResponseDto;
import com.example.qtifood.entities.Wishlist;

import java.util.List;
import java.util.stream.Collectors;

public class WishlistMapper {

    public static WishlistResponseDto toDto(Wishlist wishlist) {
        if (wishlist == null) {
            return null;
        }

        return WishlistResponseDto.builder()
                .id(wishlist.getId())
                .customerId(wishlist.getCustomer().getId())
                .store(StoreMapper.toDto(wishlist.getStore()))
                .createdAt(wishlist.getCreatedAt())
                .build();
    }

    public static List<WishlistResponseDto> toDtoList(List<Wishlist> wishlists) {
        if (wishlists == null) {
            return null;
        }
        return wishlists.stream()
                .map(WishlistMapper::toDto)
                .collect(Collectors.toList());
    }
}