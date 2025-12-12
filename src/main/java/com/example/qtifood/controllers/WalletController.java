package com.example.qtifood.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.qtifood.dtos.Wallets.*;
import com.example.qtifood.enums.TransactionType;
import com.example.qtifood.services.WalletService;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {
    
    private final WalletService walletService;
     
    /**
     * Get current user's wallet balance and information
     */
    @GetMapping("/{userId}")
    public ResponseEntity<WalletResponseDto> getWallet(@PathVariable String userId) {
        WalletResponseDto wallet = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(wallet);
    }
    
    /**
     * Initialize a new wallet for a user
     */
    @PostMapping("/{userId}/initialize")
    public ResponseEntity<WalletResponseDto> initializeWallet(@PathVariable String userId) {
        WalletResponseDto wallet = walletService.initializeWallet(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
    }
    
    /**
     * Deposit money into wallet
     */
    @PostMapping("/{userId}/deposit")
    public ResponseEntity<WalletResponseDto> deposit(
            @PathVariable String userId,
            @Valid @RequestBody DepositRequestDto request) {
        WalletResponseDto wallet = walletService.deposit(userId, request.getAmount(), request.getDescription());
        return ResponseEntity.ok(wallet);
    }

      @PostMapping("/{userId}/withdraw")
    public ResponseEntity<WalletResponseDto> withdraw(
            @PathVariable String userId,
            @Valid @RequestBody WithdrawalRequestDto request) {
        WalletResponseDto wallet = walletService.withdraw(userId, request.getAmount(), 
                request.getBankAccount(), request.getDescription());
        return ResponseEntity.ok(wallet);
    }
    
    /**
     * Get transaction history for a user (all transactions)
     */
    @GetMapping("/{userId}/transactions")
    public ResponseEntity<?> getTransactionHistory(
            @PathVariable String userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String type) {
        
        // If pagination is requested
        if (page != null && size != null) {
            Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, 
                org.springframework.data.domain.Sort.by("createdAt").descending());
            Page<WalletTransactionResponseDto> transactions = walletService.getTransactionHistoryPaginated(userId, pageable);
            return ResponseEntity.ok(transactions);
        }
        
        // If filter by type is requested
        if (type != null && !type.isEmpty()) {
            try {
                TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
                return ResponseEntity.ok(walletService.getTransactionsByType(userId, transactionType));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid transaction type: " + type);
            }
        }
        
        // Return all transactions
        return ResponseEntity.ok(walletService.getTransactionHistory(userId));
    }
    
    /**
     * Get transactions filtered by type
     */
    @GetMapping("/{userId}/transactions/type/{type}")
    public ResponseEntity<?> getTransactionsByType(
            @PathVariable String userId,
            @PathVariable String type) {
        try {
            TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
            return ResponseEntity.ok(walletService.getTransactionsByType(userId, transactionType));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid transaction type: " + type);
        }
    }
}
