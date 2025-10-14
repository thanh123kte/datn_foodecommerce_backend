package com.example.qtifood.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.qtifood.dtos.ProductImages.CreateProductImageDto;
import com.example.qtifood.dtos.ProductImages.UpdateProductImageDto;
import com.example.qtifood.dtos.ProductImages.ProductImageResponseDto;
import com.example.qtifood.services.ProductImageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/product-images")
@RequiredArgsConstructor
@Validated
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping
    public ResponseEntity<ProductImageResponseDto> createProductImage(@Valid @RequestBody CreateProductImageDto dto) {
        ProductImageResponseDto productImage = productImageService.createProductImage(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productImage);
    }

    @GetMapping
    public ResponseEntity<List<ProductImageResponseDto>> getAllProductImages() {
        return ResponseEntity.ok(productImageService.getAllProductImages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductImageResponseDto> getProductImageById(@PathVariable Long id) {
        return ResponseEntity.ok(productImageService.getProductImageById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductImageResponseDto>> getProductImagesByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.getProductImagesByProductId(productId));
    }

    @GetMapping("/product/{productId}/primary")
    public ResponseEntity<ProductImageResponseDto> getPrimaryImageByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.getPrimaryImageByProductId(productId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductImageResponseDto> updateProductImage(@PathVariable Long id,
                                                                    @Valid @RequestBody UpdateProductImageDto dto) {
        return ResponseEntity.ok(productImageService.updateProductImage(id, dto));
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
    public ResponseEntity<Void> deleteProductImagesByProductId(@PathVariable Long productId) {
        productImageService.deleteProductImagesByProductId(productId);
        return ResponseEntity.noContent().build();
    }
}