package com.example.qtifood.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.qtifood.enums.ProductStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_products_store_id", columnList = "store_id"),
        @Index(name = "idx_products_category_id", columnList = "category_id"),
        @Index(name = "idx_products_status", columnList = "status")
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_product_store",
            foreignKeyDefinition = "FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE"
        ))
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_product_category",
            foreignKeyDefinition = "FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE"
        ))
    private Categories category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_category_id",
        foreignKey = @ForeignKey(
            name = "fk_product_store_category",
            foreignKeyDefinition = "FOREIGN KEY (store_category_id) REFERENCES store_categories(id) ON DELETE SET NULL"
        ))
    private StoreCategory storeCategory;

    @Column(length = 150, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "discount_price", precision = 12, scale = 2)
    private BigDecimal discountPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ProductStatus status = ProductStatus.AVAILABLE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== ONE-TO-MANY CASCADE RELATIONSHIPS ==========
    
    // Product images
    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProductImage> productImages = new HashSet<>();

    // Order items for this product
    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems = new HashSet<>();

    // Cart items for this product
    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<CartItem> cartItems = new HashSet<>();
}
