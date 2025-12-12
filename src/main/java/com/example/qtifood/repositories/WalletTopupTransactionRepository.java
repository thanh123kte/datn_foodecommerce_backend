package com.example.qtifood.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.qtifood.entities.WalletTopupTransaction;

@Repository
public interface WalletTopupTransactionRepository extends JpaRepository<WalletTopupTransaction, Long> {
    Optional<WalletTopupTransaction> findByProviderTransactionId(String providerTransactionId);
}
