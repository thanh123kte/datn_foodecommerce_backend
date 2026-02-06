package com.example.qtifood.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.qtifood.dtos.ProductImages.ProductImageResponseDto;
import com.example.qtifood.services.ProductImageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/product-images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping(value = "/upload/{productId}", consumes = "multipart/form-data")
    public ResponseEntity<List<ProductImageResponseDto>> uploadAndSaveProductImages(
            @PathVariable Long productId,
            @RequestPart("files") List<org.springframework.web.multipart.MultipartFile> files) {
        List<ProductImageResponseDto> productImages = productImageService.uploadAndSaveProductImages(productId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(productImages);
    }

    @PostMapping(value = "/add-more/{productId}", consumes = "multipart/form-data")
    public ResponseEntity<List<ProductImageResponseDto>> addMoreProductImages(
            @PathVariable Long productId,
            @RequestPart("files") List<org.springframework.web.multipart.MultipartFile> files) {
        List<ProductImageResponseDto> productImages = productImageService.addMoreProductImages(productId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(productImages);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductImageResponseDto>> getProductImagesByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.getProductImagesByProductId(productId));
    }

    @PutMapping("/{id}/set-primary")
    public ResponseEntity<ProductImageResponseDto> setPrimaryImage(@PathVariable Long id) {
        return ResponseEntity.ok(productImageService.setPrimaryImage(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductImage(@PathVariable Long id) {
        productImageService.deleteProductImage(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> deleteAllProductImages(@PathVariable Long productId) {
        productImageService.deleteAllProductImages(productId);
        return ResponseEntity.noContent().build();
    }
}