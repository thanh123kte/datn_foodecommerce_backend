package com.example.qtifood.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.qtifood.dtos.Products.CreateProductDto;
import com.example.qtifood.dtos.Products.UpdateProductDto;
import com.example.qtifood.dtos.Products.ProductResponseDto;
import com.example.qtifood.entities.Product;
import com.example.qtifood.entities.Store;
import com.example.qtifood.entities.StoreCategory;
import com.example.qtifood.enums.ProductStatus;
import com.example.qtifood.entities.Categories;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.exceptions.EntityDuplicateException;
import com.example.qtifood.repositories.ProductRepository;
import com.example.qtifood.repositories.StoreRepository;
import com.example.qtifood.repositories.CategoriesRepository;
import com.example.qtifood.repositories.StoreCategoryRepository;
import com.example.qtifood.services.ProductService;
import com.example.qtifood.services.ProductImageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final CategoriesRepository categoriesRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final ProductImageService productImageService;

    @Override
    public ProductResponseDto createProduct(CreateProductDto dto) {
        // Check if product name already exists in the same store
        if (productRepository.existsByStoreIdAndNameIgnoreCase(dto.storeId(), dto.name())) {
            throw new EntityDuplicateException("Product with name '" + dto.name() + "' already exists in this store");
        }

        Store store = storeRepository.findById(dto.storeId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + dto.storeId()));

        StoreCategory storeCategory = storeCategoryRepository.findById(dto.storeCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Store category not found with id: " + dto.storeCategoryId()));
        
        // Validate that store category belongs to the specified store
        if (!storeCategory.getStore().getId().equals(dto.storeId())) {
            throw new IllegalArgumentException("Store category does not belong to the specified store");
        }

        Product product = Product.builder()
                .store(store)
                .storeCategory(storeCategory)
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .discountPrice(dto.discountPrice())
                .status(dto.status() != null ? dto.status() : ProductStatus.AVAILABLE)
                .build();

        return toDto(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toDto(product);
    }

    @Override
    public ProductResponseDto updateProduct(Long id, UpdateProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (dto.storeCategoryId() != null) {
            StoreCategory storeCategory = storeCategoryRepository.findById(dto.storeCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Store category not found with id: " + dto.storeCategoryId()));
            
            // Validate that store category belongs to the product's store
            if (!storeCategory.getStore().getId().equals(product.getStore().getId())) {
                throw new IllegalArgumentException("Store category does not belong to the product's store");
            }
            
            product.setStoreCategory(storeCategory);
        }

        if (dto.name() != null) {
            // Check if new name already exists in the same store (excluding current product)
            if (productRepository.existsByStoreIdAndNameIgnoreCaseAndIdNot(
                    product.getStore().getId(), dto.name(), id)) {
                throw new EntityDuplicateException("Product with name '" + dto.name() + "' already exists in this store");
            }
            product.setName(dto.name());
        }

        if (dto.description() != null) {
            product.setDescription(dto.description());
        }

        if (dto.price() != null) {
            product.setPrice(dto.price());
        }

        if (dto.discountPrice() != null) {
            product.setDiscountPrice(dto.discountPrice());
        }

        if (dto.status() != null) {
            product.setStatus(dto.status());
        }

        return toDto(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        // Xóa tất cả ảnh của product trước khi xóa product
        productImageService.deleteAllProductImages(id);
        
        // Sau đó xóa product
        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByStore(Long storeId) {
        return productRepository.findByStoreId(storeId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByStatus(ProductStatus status) {
        return productRepository.findByStatus(status)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> searchProductsByName(String keyword) {
        return productRepository.findByNameOrCategoryNameContainingIgnoreCase(keyword)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByStoreAndStatus(Long storeId, ProductStatus status) {
        return productRepository.findByStoreIdAndStatus(storeId, status)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto updateProductStatus(Long id, ProductStatus status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        product.setStatus(status);
        return toDto(productRepository.save(product));
    }

    private ProductResponseDto toDto(Product product) {
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