package com.example.qtifood.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.qtifood.dtos.ProductImages.CreateProductImageDto;
import com.example.qtifood.dtos.ProductImages.UpdateProductImageDto;
import com.example.qtifood.dtos.ProductImages.ProductImageResponseDto;
import com.example.qtifood.entities.Product;
import com.example.qtifood.entities.ProductImage;
import com.example.qtifood.repositories.ProductImageRepository;
import com.example.qtifood.repositories.ProductRepository;
import com.example.qtifood.services.ProductImageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    @Override
    public ProductImageResponseDto createProductImage(CreateProductImageDto dto) {
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + dto.productId()));

        // If this image is set as primary, reset other primary images for this product
        if (Boolean.TRUE.equals(dto.isPrimary())) {
            productImageRepository.resetPrimaryImageForProduct(dto.productId());
        }

        // If this is the first image for the product, set it as primary automatically
        boolean isFirstImage = !productImageRepository.existsByProductIdAndIsPrimaryTrue(dto.productId());
        
        ProductImage productImage = ProductImage.builder()
                .product(product)
                .imageUrl(dto.imageUrl())
                .isPrimary(Boolean.TRUE.equals(dto.isPrimary()) || isFirstImage)
                .build();

        return toDto(productImageRepository.save(productImage));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageResponseDto> getAllProductImages() {
        return productImageRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductImageResponseDto getProductImageById(Long id) {
        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product image not found: " + id));
        return toDto(productImage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageResponseDto> getProductImagesByProductId(Long productId) {
        return productImageRepository.findByProductIdOrderByIsPrimaryDescCreatedAtAsc(productId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductImageResponseDto getPrimaryImageByProductId(Long productId) {
        ProductImage productImage = productImageRepository.findByProductIdAndIsPrimaryTrue(productId)
                .orElseThrow(() -> new IllegalArgumentException("Primary image not found for product: " + productId));
        return toDto(productImage);
    }

    @Override
    public ProductImageResponseDto updateProductImage(Long id, UpdateProductImageDto dto) {
        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product image not found: " + id));

        // If setting as primary, reset other primary images for this product
        if (Boolean.TRUE.equals(dto.isPrimary())) {
            productImageRepository.resetPrimaryImageForProduct(productImage.getProduct().getId());
        }

        productImage.setImageUrl(dto.imageUrl());
        if (dto.isPrimary() != null) {
            productImage.setIsPrimary(dto.isPrimary());
        }

        return toDto(productImageRepository.save(productImage));
    }

    @Override
    public void deleteProductImage(Long id) {
        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product image not found: " + id));
        
        Long productId = productImage.getProduct().getId();
        boolean wasPrimary = Boolean.TRUE.equals(productImage.getIsPrimary());
        
        productImageRepository.delete(productImage);
        
        // If we deleted the primary image, set the oldest remaining image as primary
        if (wasPrimary) {
            List<ProductImage> remainingImages = productImageRepository.findByProductIdOrderByIsPrimaryDescCreatedAtAsc(productId);
            if (!remainingImages.isEmpty()) {
                ProductImage newPrimary = remainingImages.get(0);
                newPrimary.setIsPrimary(true);
                productImageRepository.save(newPrimary);
            }
        }
    }

    @Override
    public void deleteProductImagesByProductId(Long productId) {
        productImageRepository.deleteByProductId(productId);
    }

    @Override
    public ProductImageResponseDto setPrimaryImage(Long imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Product image not found: " + imageId));

        // Reset all primary images for this product
        productImageRepository.resetPrimaryImageForProduct(productImage.getProduct().getId());
        
        // Set this image as primary
        productImage.setIsPrimary(true);
        
        return toDto(productImageRepository.save(productImage));
    }

    private ProductImageResponseDto toDto(ProductImage productImage) {
        return ProductImageResponseDto.builder()
                .id(productImage.getId())
                .productId(productImage.getProduct().getId())
                .productName(productImage.getProduct().getName())
                .imageUrl(productImage.getImageUrl())
                .isPrimary(productImage.getIsPrimary())
                .createdAt(productImage.getCreatedAt())
                .updatedAt(productImage.getUpdatedAt())
                .build();
    }
}