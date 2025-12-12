package com.example.qtifood.dtos.Wallets;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopUpRequestDto {
    @NotNull
    private BigDecimal amount;
    @Builder.Default
    private String currency = "VND";
    private String returnUrl; // optional redirect after payment
}
