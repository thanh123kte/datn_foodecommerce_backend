package com.example.qtifood.dtos.Voucher;

import com.example.qtifood.entities.DiscountType;
import com.example.qtifood.entities.DiscountStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateVoucherDto(
        String title,
        String description,
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal minOrderValue,
        BigDecimal maxDiscount,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer usageLimit,
        DiscountStatus status,
        Boolean isActive
) {}
