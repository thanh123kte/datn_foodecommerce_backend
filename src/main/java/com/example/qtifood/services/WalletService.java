package com.example.qtifood.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.qtifood.dtos.Wallets.*;
import com.example.qtifood.enums.TransactionStatus;
import com.example.qtifood.enums.TransactionType;

public interface WalletService {
    // Wallet operations
    WalletResponseDto getWalletByUserId(String userId);
    WalletResponseDto getWalletById(Long walletId);
    WalletResponseDto initializeWallet(String userId);
    
    // Deposit
    WalletResponseDto deposit(String userId, BigDecimal amount, String description);
    
    // Withdrawal
    WalletResponseDto withdraw(String userId, BigDecimal amount, String bankAccount, String description);
    
    // Admin approval/rejection for withdrawals
    com.example.qtifood.entities.WalletTransaction approveWithdrawal(Long transactionId);
    com.example.qtifood.entities.WalletTransaction rejectWithdrawal(Long transactionId, String reason);

    // Transaction history
    List<WalletTransactionResponseDto> getTransactionHistory(String userId);
    Page<WalletTransactionResponseDto> getTransactionHistoryPaginated(String userId, Pageable pageable);
    List<WalletTransactionResponseDto> getTransactionsByType(String userId, TransactionType type);
    List<WalletTransactionResponseDto> getTransactionsByStatus(TransactionStatus status);
    Page<WalletTransactionResponseDto> getTransactionsByStatusPaginated(TransactionStatus status, Pageable pageable);
    
    // Admin withdrawal requests by status
    List<WalletTransactionResponseDto> getWithdrawalsByStatus(TransactionStatus status);
    Page<WalletTransactionResponseDto> getWithdrawalsByStatusPaginated(TransactionStatus status, Pageable pageable);
    
    // Internal transaction (for orders, payments, etc.)
    void recordTransaction(String userId, TransactionType type, BigDecimal amount, 
                         String description, String referenceId, String referenceType);
}
