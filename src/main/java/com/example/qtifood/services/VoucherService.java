package com.example.qtifood.services;

import com.example.qtifood.dtos.Voucher.*;
import java.util.List;

public interface VoucherService {
    VoucherResponseDto create(CreateVoucherDto dto);
    List<VoucherResponseDto> getAll();
    VoucherResponseDto getById(Long id);
    VoucherResponseDto update(Long id, UpdateVoucherDto dto);
    void delete(Long id);
    List<VoucherResponseDto> getBySeller(Long sellerId);
    
    // Quản lý usage
    VoucherResponseDto incrementUsage(Long voucherId);
    VoucherResponseDto decrementUsage(Long voucherId);
    
    // Kiểm tra và validate voucher
    VoucherResponseDto validateVoucher(String code);
    boolean isVoucherExpired(Long voucherId);
    boolean isVoucherUsageLimitReached(Long voucherId);
}
