package com.example.qtifood.services;

import java.math.BigDecimal;

import com.example.qtifood.dtos.WalletTopup.SePayWebhookDto;
import com.example.qtifood.dtos.WalletTopup.TopupResponseDto;

/**
 * Service để tích hợp sePay cho ví QTI
 * sePay là payment gateway để check tài khoản có tiền mới không
 * Không tạo link thanh toán, client tự chuyển tiền ngoài app
 */
public interface SePayService {
    
    /**
     * Khởi tạo một yêu cầu top-up trên ví QTI
     * Client sẽ chuyển tiền vào tài khoản sePay ngoài app
     * @param userId Firebase UID của user
     * @param amount Số tiền muốn nạp
     * @return TopupResponseDto chứa providerTransactionId (reference)
     */
    TopupResponseDto createTopup(String userId, BigDecimal amount);
    
    /**
     * Xử lý webhook từ sePay khi phát hiện tiền vào tài khoản
     * @param webhook SePayWebhookDto từ sePay (transactionId + status)
     */
    void handleWebhook(SePayWebhookDto webhook);
}
