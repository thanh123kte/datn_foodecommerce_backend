package com.example.qtifood.mappers;

import com.example.qtifood.dtos.Products.ProductResponseDto;
import com.example.qtifood.entities.Product;

public class ProductMapper {

    public static ProductResponseDto toDto(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponseDto.builder()
                .id(product.getId())
                .storeId(product.getStore().getId())
                .storeName(product.getStore().getName())
                .categoryId(product.getStoreCategory().getCategory().getId())
                .categoryName(product.getStoreCategory().getCategory().getName())
                .storeCategoryId(product.getStoreCategory().getId())
                .storeCategoryName(product.getStoreCategory().getName())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}