package com.example.qtifood.services;

import java.util.List;

import com.example.qtifood.dtos.ProductImages.ProductImageResponseDto;

public interface ProductImageService {
    
    List<ProductImageResponseDto> getProductImagesByProductId(Long productId);
    
    void deleteProductImage(Long id);
    
    void deleteAllProductImages(Long productId);
    
    ProductImageResponseDto setPrimaryImage(Long imageId);
    
    List<ProductImageResponseDto> uploadAndSaveProductImages(Long productId, List<org.springframework.web.multipart.MultipartFile> files);
}