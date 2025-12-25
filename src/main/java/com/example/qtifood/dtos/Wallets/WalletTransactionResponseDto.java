package com.example.qtifood.dtos.Wallets;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.qtifood.enums.TransactionStatus;

import com.example.qtifood.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionResponseDto {
    private Long id;
    private Long walletId;
    private String userId;
    private TransactionType transactionType;
    private TransactionStatus status;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private String referenceId;
    private String referenceType;
    private LocalDateTime createdAt;
}
