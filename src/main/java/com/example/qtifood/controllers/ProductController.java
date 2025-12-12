package com.example.qtifood.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.qtifood.dtos.Products.CreateProductDto;
import com.example.qtifood.dtos.Products.UpdateProductDto;
import com.example.qtifood.enums.AdminStatus;
import com.example.qtifood.enums.ProductStatus;
import com.example.qtifood.dtos.Products.ProductResponseDto;
import com.example.qtifood.services.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody CreateProductDto dto) {
        ProductResponseDto product = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id,
                                                           @Valid @RequestBody UpdateProductDto dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(productService.getProductsByStore(storeId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByStatus(@PathVariable ProductStatus status) {
        return ResponseEntity.ok(productService.getProductsByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(@RequestParam("q") String name) {
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

    @GetMapping("/store/{storeId}/status/{status}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByStoreAndStatus(@PathVariable Long storeId,
                                                                              @PathVariable ProductStatus status) {
        return ResponseEntity.ok(productService.getProductsByStoreAndStatus(storeId, status));
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<ProductResponseDto> updateProductStatus(@PathVariable Long id,
                                                                @PathVariable ProductStatus status) {
        return ResponseEntity.ok(productService.updateProductStatus(id, status));
    }

    // API dành riêng cho ADMIN để cập nhật admin status (ACTIVE hoặc BANNED)
    @PutMapping("/{id}/admin-status/{adminStatus}")
    public ResponseEntity<ProductResponseDto> updateProductAdminStatus(
            @PathVariable Long id,
            @PathVariable AdminStatus adminStatus) {
        return ResponseEntity.ok(productService.updateProductAdminStatus(id, adminStatus));
    }
}