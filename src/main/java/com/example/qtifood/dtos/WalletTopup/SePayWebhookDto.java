package com.example.qtifood.dtos.WalletTopup;

import java.math.BigDecimal;
import lombok.*;

/**
 * DTO để nhận webhook từ sePay (đã mở rộng theo payload mẫu từ ngân hàng/sePay)
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SePayWebhookDto {
    // possible transaction identifier from sePay
    private String transactionId;
    // sePay reference code / bank reference
    private String referenceCode;
    private String status; // SUCCESS, FAILED, PENDING, CANCELLED
    private String description;
    private String errorCode;
    private String errorMessage;

    // fields from sample payloads
    private String gateway;
    private String transactionDate; // raw date string
    private String accountNumber; // destination account on sePay
    private String subAccount;
    private String code;
    private String content; // message content, may include "nap tien vao qtifood"
    private String transferType;
    private String transferDescription;
    private BigDecimal transferAmount;
    private BigDecimal accumulated;
    private Long id; // external id
}
