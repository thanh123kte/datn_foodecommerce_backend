package com.example.qtifood.dtos.WalletTopup;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

/**
 * Response khi user yêu cầu top-up
 * sePay không cung cấp payment URL, user tự chuyển tiền vào tài khoản sePay ngoài app
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TopupResponseDto {
    private Long id;
    private BigDecimal amount;
    private String providerTransactionId; // Reference ID để track trạng thái
    private String paymentUrl; // null, user tự chuyển tiền ngoài app
    private String status; // PENDING, SUCCESS, FAILED
    private LocalDateTime createdAt;
}
