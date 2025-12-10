package com.example.qtifood.dtos.Wallets;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponseDto {
    private Long id;
    private String userId;
    private BigDecimal balance;
    private BigDecimal totalDeposited;
    private BigDecimal totalWithdrawn;
    private BigDecimal totalEarned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
