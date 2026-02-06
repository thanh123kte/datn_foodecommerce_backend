package com.example.qtifood.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "search_history",
    indexes = {
        @Index(name = "idx_search_history_user_id", columnList = "user_id"),
        @Index(name = "idx_search_history_keyword", columnList = "keyword"),
        @Index(name = "idx_search_history_created_at", columnList = "created_at")
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_search_history_user",
            foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE"
        ))
    private User user;

    @Column(length = 255, nullable = false)
    private String keyword;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}