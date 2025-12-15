package com.example.qtifood.controllers;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.qtifood.dtos.WalletTopup.CreateTopupRequestDto;
import com.example.qtifood.dtos.WalletTopup.SePayWebhookDto;
import com.example.qtifood.dtos.WalletTopup.TopupResponseDto;
import com.example.qtifood.services.SePayService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller để xử lý tích hợp sePay cho ví QTI
 * Chỉ xử lý top-up ví (nạp tiền vào ví ảo QTI)
 */
@RestController
@RequestMapping("/api/sepay")
@RequiredArgsConstructor
public class SePayController {
    
    private final SePayService sePayService;
    
    /**
     * Khởi tạo yêu cầu top-up ví QTI
     * Client sẽ chuyển tiền vào tài khoản sePay ngoài app
     * 
     * @param userId Firebase UID của user
     * @param request CreateTopupRequestDto chứa amount
     * @return TopupResponseDto với providerTransactionId (để track)
     */
    @PostMapping("/topup/{userId}")
    public ResponseEntity<TopupResponseDto> createTopup(
            @PathVariable String userId,
            @Valid @RequestBody CreateTopupRequestDto request) {
        
        BigDecimal amount = request.getAmount();
        TopupResponseDto response = sePayService.createTopup(userId, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Webhook từ sePay khi tiền đã vào tài khoản
     * sePay thông báo transaction ID + status khi phát hiện tiền mới
     * Backend check và credit wallet tương ứng
     * 
     * @param webhook SePayWebhookDto từ sePay
     * @return HTTP 200 OK
     */
    @PostMapping("/webhook")
    public ResponseEntity<Object> handleWebhook(@RequestBody SePayWebhookDto webhook) {
        try {
            sePayService.handleWebhook(webhook);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Don't return 500 to sePay/bank; log and return 200 so provider won't keep retrying.
            // Also, return a small JSON body for debugging if someone calls this endpoint manually.
            return ResponseEntity.ok().body(java.util.Map.of("status", "error", "message", e.getMessage()));
        }
    }
    
    /**
     * TEST endpoint để simulate SePay webhook
     * Dùng để test xử lý webhook mà không cần chờ SePay thực tế
     * 
     * @param providerTransactionId Provider transaction ID cần test
     * @return HTTP 200 OK
     */
    @PostMapping("/webhook/test/{providerTransactionId}")
    public ResponseEntity<Object> testWebhook(@PathVariable String providerTransactionId) {
        // Tạo webhook giả lập với transaction ID
        SePayWebhookDto testWebhook = SePayWebhookDto.builder()
                .transactionId(providerTransactionId)
                .referenceCode(providerTransactionId)
                .status("SUCCESS")
                .content("Test topup: " + providerTransactionId)
                .transferDescription("Test nap tien vao qtifood")
                .transferAmount(new BigDecimal("10000"))
                .build();
        
        try {
            sePayService.handleWebhook(testWebhook);
            return ResponseEntity.ok().body(java.util.Map.of(
                "status", "success", 
                "message", "Test webhook processed successfully",
                "providerTransactionId", providerTransactionId
            ));
        } catch (Exception e) {
            return ResponseEntity.ok().body(java.util.Map.of(
                "status", "error", 
                "message", e.getMessage(),
                "providerTransactionId", providerTransactionId
            ));
        }
    }
}

