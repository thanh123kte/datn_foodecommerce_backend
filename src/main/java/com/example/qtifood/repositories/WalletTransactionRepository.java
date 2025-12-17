package com.example.qtifood.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.qtifood.entities.WalletTransaction;
import com.example.qtifood.enums.TransactionStatus;
import com.example.qtifood.enums.TransactionType;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWalletId(Long walletId);
    List<WalletTransaction> findByWalletIdOrderByIdDesc(Long walletId);
    Page<WalletTransaction> findByWalletId(Long walletId, Pageable pageable);
    List<WalletTransaction> findByWalletIdAndTransactionType(Long walletId, TransactionType type);
    List<WalletTransaction> findByReferenceIdAndReferenceType(String referenceId, String referenceType);
    List<WalletTransaction> findByStatusOrderByCreatedAtDesc(TransactionStatus status);
    Page<WalletTransaction> findByStatus(TransactionStatus status, Pageable pageable);
    List<WalletTransaction> findByTransactionTypeAndStatusOrderByCreatedAtDesc(TransactionType type, TransactionStatus status);
    Page<WalletTransaction> findByTransactionTypeAndStatus(TransactionType type, TransactionStatus status, Pageable pageable);
}
