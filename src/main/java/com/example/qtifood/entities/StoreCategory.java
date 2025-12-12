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
        @Index(name = "idx_store_categories_parent_id", columnList = "parent_id")
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
        foreignKey = @ForeignKey(name = "fk_store_categories_store")
    )
    @ToString.Exclude
    private Store store;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "parent_id",
        foreignKey = @ForeignKey(name = "fk_store_categories_parent_category")
    )
    @ToString.Exclude
    private Categories parentCategory; 

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
