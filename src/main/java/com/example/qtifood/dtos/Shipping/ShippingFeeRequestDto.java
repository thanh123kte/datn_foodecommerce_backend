package com.example.qtifood.dtos.Shipping;

import java.math.BigDecimal;

public record ShippingFeeRequestDto(
        BigDecimal storeLatitude,
        BigDecimal storeLongitude,
        BigDecimal recipientLatitude,
        BigDecimal recipientLongitude
) {}
