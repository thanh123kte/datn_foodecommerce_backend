package com.example.qtifood.dtos.Voucher;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.qtifood.enums.DiscountStatus;
import com.example.qtifood.enums.DiscountType;

public record CreateVoucherDto(
        String code,
        String title,
        String description,
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal minOrderValue,
        BigDecimal maxDiscount,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer usageLimit,
        Long storeId,
        DiscountStatus status,
        Boolean isActive,
        Boolean isCreatedByAdmin
) {}
