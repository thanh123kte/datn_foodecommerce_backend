package com.example.qtifood.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "store_categories",
    indexes = {
        @Index(name = "idx_store_categories_store_id", columnList = "store_id"),
        @Index(name = "idx_store_categories_category_id", columnList = "category_id")
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class StoreCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "store_id",
        nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_store_categories_store",
            foreignKeyDefinition = "FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE"
        )
    )
    @ToString.Exclude
    private Store store;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "category_id",
        foreignKey = @ForeignKey(
            name = "fk_store_categories_category",
            foreignKeyDefinition = "FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE"
        )
    )
    @ToString.Exclude
    private Categories category; 

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
