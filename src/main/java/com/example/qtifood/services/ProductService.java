package com.example.qtifood.services;

import java.util.List;

import com.example.qtifood.dtos.Products.CreateProductDto;
import com.example.qtifood.dtos.Products.UpdateProductDto;
import com.example.qtifood.enums.ProductStatus;
import com.example.qtifood.dtos.Products.ProductResponseDto;

public interface ProductService {

    // Tạo mới sản phẩm
    ProductResponseDto createProduct(CreateProductDto dto);

    // Lấy tất cả sản phẩm
    List<ProductResponseDto> getAllProducts();

    // Lấy sản phẩm theo id
    ProductResponseDto getProductById(Long id);

    // Cập nhật sản phẩm
    ProductResponseDto updateProduct(Long id, UpdateProductDto dto);

    // Xóa sản phẩm
    void deleteProduct(Long id);

    // Lấy sản phẩm theo store
    List<ProductResponseDto> getProductsByStore(Long storeId);

    // Lấy sản phẩm theo category
    List<ProductResponseDto> getProductsByCategory(Long categoryId);

    // Lấy sản phẩm theo status
    List<ProductResponseDto> getProductsByStatus(ProductStatus status);

    // Tìm kiếm sản phẩm theo tên
    List<ProductResponseDto> searchProductsByName(String name);

    // Lấy sản phẩm theo store và status
    List<ProductResponseDto> getProductsByStoreAndStatus(Long storeId, ProductStatus status);

    // Cập nhật trạng thái sản phẩm
    ProductResponseDto updateProductStatus(Long id, ProductStatus status);
}