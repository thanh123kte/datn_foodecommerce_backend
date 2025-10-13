package com.example.qtifood.dtos.Voucher;

import com.example.qtifood.entities.DiscountType;
import com.example.qtifood.entities.DiscountStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VoucherResponseDto(
        Long id,
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
        Long sellerId,
        String sellerName,
        DiscountStatus status,
        Boolean isActive,
        Boolean isCreatedByAdmin,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
