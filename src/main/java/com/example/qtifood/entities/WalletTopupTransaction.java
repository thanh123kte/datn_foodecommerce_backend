package com.example.qtifood.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity để lưu trữ thông tin top-up ví QTI từ sePay
 */
@Entity
@Table(name = "wallet_topup_transactions", indexes = {
    @Index(name = "idx_topup_wallet", columnList = "wallet_id"),
    @Index(name = "idx_topup_provider_tx", columnList = "provider_transaction_id"),
    @Index(name = "idx_topup_status", columnList = "status"),
    @Index(name = "idx_topup_created_at", columnList = "created_at")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class WalletTopupTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_topup_wallet"))
    private Wallet wallet;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    /**
     * Transaction ID từ sePay (để track trạng thái ngoài)
     */
    @Column(name = "provider_transaction_id", length = 100, unique = true)
    private String providerTransactionId;
    
    /**
     * URL để người dùng thanh toán trên sePay
     */
    @Column(name = "payment_url", length = 500)
    private String paymentUrl;
    
    /**
     * Trạng thái: PENDING, SUCCESS, FAILED, CANCELLED
     */
    @Column(nullable = false, length = 20)
    private String status; // "PENDING", "SUCCESS", "FAILED", "CANCELLED"
    
    /**
     * Lý do thất bại (nếu có)
     */
    @Column(name = "failure_reason", length = 500)
    private String failureReason;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
