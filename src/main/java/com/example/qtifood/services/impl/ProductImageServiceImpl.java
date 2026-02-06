package com.example.qtifood.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.qtifood.dtos.ProductImages.ProductImageResponseDto;
import com.example.qtifood.entities.Product;
import com.example.qtifood.entities.ProductImage;
import com.example.qtifood.exceptions.ResourceNotFoundException;
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
    @Transactional(readOnly = true)
    public List<ProductImageResponseDto> getProductImagesByProductId(Long productId) {
        return productImageRepository.findByProductIdOrderByIsPrimaryDescCreatedAtAsc(productId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProductImage(Long id) {
        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product image not found with id: " + id));
        
        Long productId = productImage.getProduct().getId();
        boolean wasPrimary = Boolean.TRUE.equals(productImage.getIsPrimary());
        
        // Delete physical file
        deletePhysicalFile(productImage.getImageUrl());
        
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
    public void deleteAllProductImages(Long productId) {
        // Kiểm tra product tồn tại
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        // Lấy tất cả ảnh của product
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        
        // Xóa file vật lý trước
        for (ProductImage image : images) {
            deletePhysicalFile(image.getImageUrl());
        }
        
        // Xóa từ database
        productImageRepository.deleteByProductId(productId);
    }

    private void deletePhysicalFile(String imageUrl) {
        try {
            // Loại bỏ dấu "/" ở đầu nếu có
            String filePath = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            if (java.nio.file.Files.exists(path)) {
                java.nio.file.Files.delete(path);
            }
        } catch (java.io.IOException e) {
            // Log lỗi nhưng không throw exception để không làm gián đoạn việc xóa database
            System.err.println("Lỗi khi xóa file: " + imageUrl + " - " + e.getMessage());
        }
    }

    @Override
    public ProductImageResponseDto setPrimaryImage(Long imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Product image not found with id: " + imageId));

        // Reset all primary images for this product
        productImageRepository.resetPrimaryImageForProduct(productImage.getProduct().getId());
        
        // Set this image as primary
        productImage.setIsPrimary(true);
        
        return toDto(productImageRepository.save(productImage));
    }

    @Override
    public List<ProductImageResponseDto> uploadAndSaveProductImages(Long productId, 
            List<org.springframework.web.multipart.MultipartFile> files) {
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        String uploadDir = "uploads/products/";
        java.io.File directory = new java.io.File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        List<String> imageUrls = files.stream().map(file -> {
            try {
                // Validate file
                if (file.isEmpty()) {
                    throw new IllegalArgumentException("File không được để trống");
                }

                // Validate file type
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new IllegalArgumentException("File phải là ảnh (jpg, png, gif, etc.)");
                }

                // Generate unique filename
                String originalFilename = file.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                
                String uniqueFilename = java.util.UUID.randomUUID().toString() + "_" + 
                    (originalFilename != null ? originalFilename : "image" + fileExtension);

                // Save file
                java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir + uniqueFilename);
                java.nio.file.Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                // Return URL
                return "/" + uploadDir + uniqueFilename;

            } catch (java.io.IOException e) {
                throw new RuntimeException("Lỗi khi lưu file: " + file.getOriginalFilename(), e);
            }
        }).collect(java.util.stream.Collectors.toList());
        
        List<ProductImageResponseDto> results = new java.util.ArrayList<>();
        
        if (!imageUrls.isEmpty()) {
            productImageRepository.resetPrimaryImageForProduct(productId);
        }
        
        for (int i = 0; i < imageUrls.size(); i++) {
            ProductImage productImage = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageUrls.get(i))
                    .isPrimary(i == 0) // Ảnh đầu tiên làm primary
                    .build();
            
            ProductImage saved = productImageRepository.save(productImage);
            results.add(toDto(saved));
        }
        
        return results;
    }

    @Override
    public List<ProductImageResponseDto> addMoreProductImages(Long productId, 
            List<org.springframework.web.multipart.MultipartFile> files) {
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        // Check if product already has a primary image
        boolean hasPrimaryImage = productImageRepository.existsByProductIdAndIsPrimaryTrue(productId);
        
        String uploadDir = "uploads/products/";
        java.io.File directory = new java.io.File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        List<String> imageUrls = files.stream().map(file -> {
            try {
                // Validate file
                if (file.isEmpty()) {
                    throw new IllegalArgumentException("File không được để trống");
                }

                // Validate file type
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new IllegalArgumentException("File phải là ảnh (jpg, png, gif, etc.)");
                }

                // Generate unique filename
                String originalFilename = file.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                
                String uniqueFilename = java.util.UUID.randomUUID().toString() + "_" + 
                    (originalFilename != null ? originalFilename : "image" + fileExtension);

                // Save file
                java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir + uniqueFilename);
                java.nio.file.Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                // Return URL
                return "/" + uploadDir + uniqueFilename;

            } catch (java.io.IOException e) {
                throw new RuntimeException("Lỗi khi lưu file: " + file.getOriginalFilename(), e);
            }
        }).collect(java.util.stream.Collectors.toList());
        
        List<ProductImageResponseDto> results = new java.util.ArrayList<>();
        
        for (int i = 0; i < imageUrls.size(); i++) {
            ProductImage productImage = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageUrls.get(i))
                    // If no primary image exists, make the first new image primary
                    // Otherwise, all new images are non-primary
                    .isPrimary(!hasPrimaryImage && i == 0)
                    .build();
            
            ProductImage saved = productImageRepository.save(productImage);
            results.add(toDto(saved));
        }
        
        return results;
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