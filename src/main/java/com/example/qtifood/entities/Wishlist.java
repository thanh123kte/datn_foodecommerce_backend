package com.example.qtifood.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "wishlists",
    indexes = {
        @Index(name = "idx_wishlists_customer_id", columnList = "customer_id"),
        @Index(name = "idx_wishlists_store_id", columnList = "store_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_wishlists_customer_store", 
                         columnNames = {"customer_id", "store_id"})
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Wishlist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_wishlist_customer"))
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_wishlist_store"))
    private Store store;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}