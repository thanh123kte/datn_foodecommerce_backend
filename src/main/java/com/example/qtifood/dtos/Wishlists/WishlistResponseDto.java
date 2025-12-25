package com.example.qtifood.dtos.Wishlists;

import com.example.qtifood.dtos.Stores.StoreResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResponseDto {
    
    private Long id;
    private String customerId;
    private StoreResponseDto store;
    private LocalDateTime createdAt;
}