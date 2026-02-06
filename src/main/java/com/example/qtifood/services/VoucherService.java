package com.example.qtifood.services;

import com.example.qtifood.dtos.Voucher.*;
import com.example.qtifood.enums.DiscountType;
import java.util.List;

public interface VoucherService {
    VoucherResponseDto create(CreateVoucherDto dto);
    List<VoucherResponseDto> getAll();
    List<VoucherResponseDto> getAllNotDeleted();
    VoucherResponseDto getById(Long id);
    VoucherResponseDto update(Long id, UpdateVoucherDto dto);
    void delete(Long id);
    void softDelete(Long id);
    List<VoucherResponseDto> getByStore(Long storeId);
    List<VoucherResponseDto> getByStoreNotDeleted(Long storeId);
    List<VoucherResponseDto> getByDiscountType(DiscountType discountType);
    List<VoucherResponseDto> getByDiscountTypeNotDeleted(DiscountType discountType);
    List<VoucherResponseDto> getAdminVouchers();
    List<VoucherResponseDto> getStoreVouchers();
    List<VoucherResponseDto> getAdminVouchersNotDeleted();
    List<VoucherResponseDto> getStoreVouchersNotDeleted();

    // Quản lý usage
    VoucherResponseDto incrementUsage(Long voucherId);
    VoucherResponseDto decrementUsage(Long voucherId);

    // Kiểm tra và validate voucher
    VoucherResponseDto validateVoucher(String code);
    boolean isVoucherExpired(Long voucherId);
    boolean isVoucherUsageLimitReached(Long voucherId);
}
