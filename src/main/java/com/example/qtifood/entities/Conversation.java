package com.example.qtifood.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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
        foreignKey = @ForeignKey(name = "fk_conversation_customer"))
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_conversation_seller"))
    private User seller;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}