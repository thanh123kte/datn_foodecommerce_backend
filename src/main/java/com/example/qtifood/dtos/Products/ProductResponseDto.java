package com.example.qtifood.dtos.Products;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.qtifood.enums.AdminStatus;
import com.example.qtifood.enums.ProductStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDto {
    private Long id;
    private Long storeId;
    private String storeName;
    private Long categoryId;
    private String categoryName;
    private Long storeCategoryId;
    private String storeCategoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private ProductStatus status;
    private AdminStatus adminStatus;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
