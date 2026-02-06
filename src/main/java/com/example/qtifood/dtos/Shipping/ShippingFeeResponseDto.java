package com.example.qtifood.dtos.Shipping;

import java.math.BigDecimal;

public record ShippingFeeResponseDto(
        Double distanceKm,
        BigDecimal baseFee,
        BigDecimal additionalFee,
        BigDecimal totalFee,
        String description
) {}
