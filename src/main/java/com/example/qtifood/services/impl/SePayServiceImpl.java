package com.example.qtifood.services.impl;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.qtifood.dtos.WalletTopup.SePayWebhookDto;
import com.example.qtifood.dtos.WalletTopup.TopupResponseDto;
import com.example.qtifood.entities.Wallet;
import com.example.qtifood.entities.WalletTopupTransaction;
import com.example.qtifood.enums.TransactionType;
import com.example.qtifood.repositories.WalletRepository;
import com.example.qtifood.repositories.WalletTopupTransactionRepository;
import com.example.qtifood.services.FcmService;
import com.example.qtifood.services.SePayService;
import com.example.qtifood.services.WalletService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SePayServiceImpl implements SePayService {
    
    private static final Logger logger = LoggerFactory.getLogger(SePayServiceImpl.class);
    
    private final WalletRepository walletRepository;
    private final WalletTopupTransactionRepository topupRepository;
    private final WalletService walletService;
    private final FcmService fcmService;
    
    @Override
    @Transactional
    public TopupResponseDto createTopup(String userId, BigDecimal amount) {
        logger.info("Creating topup request for user: {}, amount: {}", userId, amount);
        
        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        
        // Get user's wallet
        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new EntityNotFoundException("Wallet not found for user: " + userId));
        
        // Generate provider transaction ID (unique reference)
        String providerTransactionId = "TOPUP_" + userId + "_" + UUID.randomUUID().toString().substring(0, 12);
        
        // sePay là gateway để check tài khoản, không tạo link thanh toán
        // Client chuyển tiền vào tài khoản sePay ngoài app
        // Backend chỉ ghi nhận pending transaction
        String paymentUrl = null; // sePay không cung cấp payment URL, client tự chuyển tiền
        
        // Create and save WalletTopupTransaction (status: PENDING)
        WalletTopupTransaction topupTransaction = WalletTopupTransaction.builder()
            .wallet(wallet)
            .amount(amount)
            .providerTransactionId(providerTransactionId)
            .paymentUrl(paymentUrl)
            .status("PENDING")
            .build();
        
        topupTransaction = topupRepository.save(topupTransaction);
        
        logger.info("Topup request created: transactionId={}, amount={}, user must transfer money to sePay account", 
            providerTransactionId, amount);
        
        return TopupResponseDto.builder()
            .id(topupTransaction.getId())
            .amount(amount)
            .providerTransactionId(providerTransactionId)
            .paymentUrl(paymentUrl) // null, client tự chuyển tiền ngoài app
            .status("PENDING")
            .createdAt(topupTransaction.getCreatedAt())
            .build();
    }
    
    @Override
    @Transactional
    public void handleWebhook(SePayWebhookDto webhook) {
        logger.info("Handling sePay webhook: transactionId={}, referenceCode={}, status={}, amount={}", 
            webhook.getTransactionId(), webhook.getReferenceCode(), webhook.getStatus(), webhook.getTransferAmount());

        // Try several strategies to locate the pending topup transaction:
        // 1) match by transactionId
        // 2) match by referenceCode
        // 3) match by amount: find most recent PENDING topup with same amount
        WalletTopupTransaction topupTx = null;

        if (webhook.getTransactionId() != null) {
            topupTx = topupRepository.findByProviderTransactionId(webhook.getTransactionId()).orElse(null);
        }

        if (topupTx == null && webhook.getReferenceCode() != null) {
            topupTx = topupRepository.findByProviderTransactionId(webhook.getReferenceCode()).orElse(null);
        }

        if (topupTx == null && webhook.getTransferAmount() != null) {
            // try to find most recent pending topup with the same amount
            java.math.BigDecimal amt = webhook.getTransferAmount();
            topupTx = topupRepository.findAll().stream()
                .filter(t -> "PENDING".equalsIgnoreCase(t.getStatus()) && amt.compareTo(t.getAmount()) == 0)
                .sorted((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .findFirst().orElse(null);
        }

        // If still not found, try to parse a TOPUP token from `content` or `description` and match by normalized id
        if (topupTx == null) {
            String content = webhook.getContent() != null ? webhook.getContent() : webhook.getTransferDescription();
            if (content != null) {
                // find token that starts with TOPUP followed by word chars or hyphens
                Pattern p = Pattern.compile("(TOPUP[\\w-]+)", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(content);
                if (m.find()) {
                    String candidate = m.group(1);
                    String candNorm = normalizeId(candidate);
                    topupTx = topupRepository.findAll().stream()
                        .filter(t -> t.getProviderTransactionId() != null)
                        .filter(t -> {
                            String storedNorm = normalizeId(t.getProviderTransactionId());
                            return storedNorm.equalsIgnoreCase(candNorm) || storedNorm.contains(candNorm) || candNorm.contains(storedNorm);
                        })
                        .sorted((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                        .findFirst().orElse(null);
                }
            }
        }
        if (topupTx == null) {
            // No matching pending topup found. Log and return silently (do not throw).
            logger.warn("Topup transaction not found for webhook. transactionId={}, referenceCode={}, amount={}",
                webhook.getTransactionId(), webhook.getReferenceCode(), webhook.getTransferAmount());
            return;
        }

        // Now process according to status
        String status = webhook.getStatus();
        if (status != null && "SUCCESS".equalsIgnoreCase(status)) {
            handleTopupSuccess(topupTx);
        } else if (status != null && "FAILED".equalsIgnoreCase(status)) {
            handleTopupFailed(topupTx, webhook.getErrorMessage());
        } else if (status != null && "CANCELLED".equalsIgnoreCase(status)) {
            topupTx.setStatus("CANCELLED");
            topupTx.setFailureReason("Payment cancelled by provider");
            topupRepository.save(topupTx);
            logger.info("Topup cancelled: {}", topupTx.getProviderTransactionId());
        } else {
            // If provider didn't send explicit status, infer success from bank transfer fields
            java.math.BigDecimal transferAmt = webhook.getTransferAmount();
            String transferType = webhook.getTransferType();
            String content = webhook.getContent() != null ? webhook.getContent() : webhook.getTransferDescription();

            boolean looksLikeDeposit = transferAmt != null && transferAmt.compareTo(BigDecimal.ZERO) > 0
                && (transferType == null || "in".equalsIgnoreCase(transferType));

            if (!looksLikeDeposit && content != null) {
                String lc = content.toLowerCase();
                if (lc.contains("nap") || lc.contains("nạp") || lc.contains("deposit") || lc.contains("nap tien") || lc.contains("nap tiền")) {
                    looksLikeDeposit = true;
                }
            }

            if (looksLikeDeposit) {
                logger.info("Inferring SUCCESS for topup based on transferAmount/transferType/content for tx={}", topupTx.getProviderTransactionId());
                handleTopupSuccess(topupTx);
            } else {
                logger.info("Webhook status not recognized or non-terminal: {} for tx={}", webhook.getStatus(), topupTx.getProviderTransactionId());
            }
        }
    }

    private String normalizeId(String s) {
        if (s == null) return null;
        return s.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    }
    
    /**
     * Xử lý top-up thành công: ghi vào ví, tạo transaction history, gửi FCM
     */
    private void handleTopupSuccess(WalletTopupTransaction topupTx) {
        try {
            Wallet wallet = topupTx.getWallet();
            String userId = wallet.getUser().getId();
            BigDecimal amount = topupTx.getAmount();
            
            // Record transaction in wallet history
            walletService.recordTransaction(
                userId,
                TransactionType.DEPOSIT,
                amount,
                "Top-up via sePay: " + topupTx.getProviderTransactionId(),
                topupTx.getProviderTransactionId(),
                "SEPAY_TOPUP"
            );
            
            // Update topup transaction status
            topupTx.setStatus("SUCCESS");
            topupRepository.save(topupTx);
            
            // Send FCM notification
            String title = "Nạp tiền thành công";
            String body = String.format("Bạn vừa nạp %,.0f VND vào ví QTI", amount);
            Map<String, String> data = Map.of(
                "amount", amount.toString(),
                "transactionId", topupTx.getProviderTransactionId()
            );
            try {
                fcmService.sendNotification(userId, title, body, "TOPUP", data);
                logger.info("FCM sent for topup success: user={}, title={}, body={}", userId, title, body);
            } catch (Exception e) {
                logger.warn("Failed to send FCM for topup success: {}", e.getMessage());
            }
            
            logger.info("Topup success: user={}, amount={}, txId={}", 
                userId, amount, topupTx.getProviderTransactionId());
            
        } catch (Exception e) {
            logger.error("Error handling topup success", e);
            topupTx.setStatus("FAILED");
            topupTx.setFailureReason("Internal error while crediting wallet: " + e.getMessage());
            topupRepository.save(topupTx);
            throw new RuntimeException("Failed to process topup success", e);
        }
    }
    
    /**
     * Xử lý top-up thất bại
     */
    private void handleTopupFailed(WalletTopupTransaction topupTx, String errorMessage) {
        topupTx.setStatus("FAILED");
        topupTx.setFailureReason(errorMessage != null ? errorMessage : "Payment failed");
        topupRepository.save(topupTx);
        logger.warn("Topup failed: {}, reason: {}", topupTx.getProviderTransactionId(), errorMessage);
    }
    
}
