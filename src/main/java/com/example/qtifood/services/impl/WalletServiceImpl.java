package com.example.qtifood.services.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.qtifood.dtos.Wallets.*;
import com.example.qtifood.entities.User;
import com.example.qtifood.entities.Wallet;
import com.example.qtifood.entities.WalletTransaction;
import com.example.qtifood.enums.TransactionType;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.repositories.WalletRepository;
import com.example.qtifood.repositories.WalletTransactionRepository;
import com.example.qtifood.services.WalletService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletServiceImpl implements WalletService {
    
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public WalletResponseDto getWalletByUserId(String userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
        return toDto(wallet);
    }
    
    @Override
    @Transactional(readOnly = true)
    public WalletResponseDto getWalletById(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
        return toDto(wallet);
    }
    
    @Override
    public WalletResponseDto initializeWallet(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        
        if (walletRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("Wallet already exists for user: " + userId);
        }
        
        Wallet wallet = Wallet.builder()
            .user(user)
            .balance(BigDecimal.ZERO)
            .totalDeposited(BigDecimal.ZERO)
            .totalWithdrawn(BigDecimal.ZERO)
            .totalEarned(BigDecimal.ZERO)
            .build();
        
        return toDto(walletRepository.save(wallet));
    }
    
    @Override
    public WalletResponseDto deposit(String userId, BigDecimal amount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
        
        BigDecimal oldBalance = wallet.getBalance();
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setTotalDeposited(wallet.getTotalDeposited().add(amount));
        
        walletRepository.save(wallet);
        
        // Record transaction
        WalletTransaction transaction = WalletTransaction.builder()
            .wallet(wallet)
            .transactionType(TransactionType.DEPOSIT)
            .amount(amount)
            .balanceBefore(oldBalance)
            .balanceAfter(wallet.getBalance())
            .description(description != null ? description : "Deposit")
            .referenceType("DEPOSIT")
            .build();
        
        transactionRepository.save(transaction);
        
        return toDto(wallet);
    }
    
    @Override
    public WalletResponseDto withdraw(String userId, BigDecimal amount, String bankAccount, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
        
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance. Current balance: " + wallet.getBalance());
        }
        
        BigDecimal oldBalance = wallet.getBalance();
        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setTotalWithdrawn(wallet.getTotalWithdrawn().add(amount));
        
        walletRepository.save(wallet);
        
        // Record transaction
        String desc = description != null ? description : "Withdrawal to " + bankAccount;
        WalletTransaction transaction = WalletTransaction.builder()
            .wallet(wallet)
            .transactionType(TransactionType.WITHDRAW)
            .amount(amount)
            .balanceBefore(oldBalance)
            .balanceAfter(wallet.getBalance())
            .description(desc)
            .referenceType("WITHDRAWAL")
            .build();
        
        transactionRepository.save(transaction);
        
        return toDto(wallet);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<WalletTransactionResponseDto> getTransactionHistory(String userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
        // Return transactions ordered by id (most recent first). Using id ordering because
        // creation time and id insertion order are aligned for top-ups and transactions.
        return transactionRepository.findByWalletIdOrderByIdDesc(wallet.getId()).stream()
            .map(this::transactionToDto)
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<WalletTransactionResponseDto> getTransactionHistoryPaginated(String userId, Pageable pageable) {
        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
        
        Page<WalletTransaction> page = transactionRepository.findByWalletId(wallet.getId(), pageable);
        return new PageImpl<>(
            page.getContent().stream().map(this::transactionToDto).toList(),
            pageable,
            page.getTotalElements()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<WalletTransactionResponseDto> getTransactionsByType(String userId, TransactionType type) {
        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
        
        return transactionRepository.findByWalletIdAndTransactionType(wallet.getId(), type).stream()
            .map(this::transactionToDto)
            .toList();
    }
    
    @Override
    public void recordTransaction(String userId, TransactionType type, BigDecimal amount,
                                 String description, String referenceId, String referenceType) {
        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
        
        BigDecimal oldBalance = wallet.getBalance();
        BigDecimal newBalance = oldBalance;
        
        // Update wallet based on transaction type
        switch (type) {
            case DEPOSIT:
                newBalance = oldBalance.add(amount);
                wallet.setTotalDeposited(wallet.getTotalDeposited().add(amount));
                break;
            case WITHDRAW:
                if (oldBalance.compareTo(amount) < 0) {
                    throw new IllegalArgumentException("Insufficient balance");
                }
                newBalance = oldBalance.subtract(amount);
                wallet.setTotalWithdrawn(wallet.getTotalWithdrawn().add(amount));
                break;
            case PAYMENT:
                if (oldBalance.compareTo(amount) < 0) {
                    throw new IllegalArgumentException("Insufficient balance for payment");
                }
                newBalance = oldBalance.subtract(amount);
                break;
            case REFUND:
            case EARN:
                newBalance = oldBalance.add(amount);
                wallet.setTotalEarned(wallet.getTotalEarned().add(amount));
                break;
            default:
                break;
        }
        
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
        
        // Record transaction
        WalletTransaction transaction = WalletTransaction.builder()
            .wallet(wallet)
            .transactionType(type)
            .amount(amount)
            .balanceBefore(oldBalance)
            .balanceAfter(newBalance)
            .description(description)
            .referenceId(referenceId)
            .referenceType(referenceType)
            .build();
        
        transactionRepository.save(transaction);
    }
    
    private WalletResponseDto toDto(Wallet wallet) {
        return WalletResponseDto.builder()
            .id(wallet.getId())
            .userId(wallet.getUser().getId())
            .balance(wallet.getBalance())
            .totalDeposited(wallet.getTotalDeposited())
            .totalWithdrawn(wallet.getTotalWithdrawn())
            .totalEarned(wallet.getTotalEarned())
            .createdAt(wallet.getCreatedAt())
            .updatedAt(wallet.getUpdatedAt())
            .build();
    }
    
    private WalletTransactionResponseDto transactionToDto(WalletTransaction transaction) {
        return WalletTransactionResponseDto.builder()
            .id(transaction.getId())
            .walletId(transaction.getWallet().getId())
            .userId(transaction.getWallet().getUser().getId())
            .transactionType(transaction.getTransactionType())
            .amount(transaction.getAmount())
            .balanceBefore(transaction.getBalanceBefore())
            .balanceAfter(transaction.getBalanceAfter())
            .description(transaction.getDescription())
            .referenceId(transaction.getReferenceId())
            .referenceType(transaction.getReferenceType())
            .createdAt(transaction.getCreatedAt())
            .build();
    }
}
