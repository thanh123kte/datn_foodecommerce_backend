package com.example.qtifood.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.qtifood.enums.TransactionType;
import com.example.qtifood.enums.TransactionStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wallet_transactions", indexes = {
    @Index(name = "idx_wallet_trans_wallet", columnList = "wallet_id"),
    @Index(name = "idx_wallet_trans_type", columnList = "transaction_type"),
    @Index(name = "idx_wallet_trans_date", columnList = "created_at")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class WalletTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_wallet_trans_wallet"))
    private Wallet wallet;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal balanceBefore;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal balanceAfter;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "reference_id")
    private String referenceId; // Order ID, Transfer ID, etc.
    
    @Column(name = "reference_type")
    private String referenceType; // ORDER, TRANSFER, DEPOSIT, etc.
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
