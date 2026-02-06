package com.example.qtifood.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "cart_items",
    indexes = {
        @Index(name = "idx_cart_items_customer_id", columnList = "customer_id"),
        @Index(name = "idx_cart_items_store_id", columnList = "store_id"),
        @Index(name = "idx_cart_items_product_id", columnList = "product_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_cart_items_customer_product", 
                         columnNames = {"customer_id", "product_id"})
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_cart_item_customer",
            foreignKeyDefinition = "FOREIGN KEY (customer_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE"
        ))
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_cart_item_store",
            foreignKeyDefinition = "FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE"
        ))
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_cart_item_product",
            foreignKeyDefinition = "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE"
        ))
    private Product product;

    @Builder.Default
    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(columnDefinition = "TEXT")
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}