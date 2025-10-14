package com.example.qtifood.services;

import java.util.List;

import com.example.qtifood.dtos.ProductImages.CreateProductImageDto;
import com.example.qtifood.dtos.ProductImages.UpdateProductImageDto;
import com.example.qtifood.dtos.ProductImages.ProductImageResponseDto;

public interface ProductImageService {
    
    ProductImageResponseDto createProductImage(CreateProductImageDto dto);
    
    List<ProductImageResponseDto> getAllProductImages();
    
    ProductImageResponseDto getProductImageById(Long id);
    
    List<ProductImageResponseDto> getProductImagesByProductId(Long productId);
    
    ProductImageResponseDto getPrimaryImageByProductId(Long productId);
    
    ProductImageResponseDto updateProductImage(Long id, UpdateProductImageDto dto);
    
    void deleteProductImage(Long id);
    
    void deleteProductImagesByProductId(Long productId);
    
    ProductImageResponseDto setPrimaryImage(Long imageId);
}