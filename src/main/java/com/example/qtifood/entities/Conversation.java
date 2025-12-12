package com.example.qtifood.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "conversations",
    indexes = {
        @Index(name = "idx_conversations_customer_id", columnList = "customer_id"),
        @Index(name = "idx_conversations_seller_id", columnList = "seller_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_conversations_customer_seller", 
                         columnNames = {"customer_id", "seller_id"})
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Conversation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_conversation_customer",
            foreignKeyDefinition = "FOREIGN KEY (customer_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE"
        ))
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_conversation_seller",
            foreignKeyDefinition = "FOREIGN KEY (seller_id) REFERENCES users(firebase_user_id) ON DELETE CASCADE"
        ))
    private User seller;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ========== ONE-TO-MANY CASCADE RELATIONSHIPS ==========
    
    // Messages in this conversation
    @Builder.Default
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Message> messages = new HashSet<>();
}