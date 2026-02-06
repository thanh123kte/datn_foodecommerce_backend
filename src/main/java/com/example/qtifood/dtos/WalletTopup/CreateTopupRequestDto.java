package com.example.qtifood.dtos.WalletTopup;

import java.math.BigDecimal;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CreateTopupRequestDto {
    private BigDecimal amount;
    // amount > 0, typically: 50.000, 100.000, 200.000, etc.
}
