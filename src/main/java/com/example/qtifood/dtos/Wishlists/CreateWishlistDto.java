package com.example.qtifood.dtos.Wishlists;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWishlistDto {
    
    @NotNull(message = "Store ID is required")
    private Long storeId;
}