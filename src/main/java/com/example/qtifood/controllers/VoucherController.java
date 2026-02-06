package com.example.qtifood.controllers;

import com.example.qtifood.dtos.Voucher.*;
import com.example.qtifood.enums.DiscountType;
import com.example.qtifood.services.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService service;

    @PostMapping
    public ResponseEntity<VoucherResponseDto> create(@RequestBody @Valid CreateVoucherDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<VoucherResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/isnot_deleted")
    public ResponseEntity<List<VoucherResponseDto>> getAllNotDeleted() {
        return ResponseEntity.ok(service.getAllNotDeleted());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoucherResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VoucherResponseDto> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateVoucherDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/soft-delete")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<VoucherResponseDto>> getByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(service.getByStore(storeId));
    }

    @GetMapping("/store/{storeId}/isnot_deleted")
    public ResponseEntity<List<VoucherResponseDto>> getByStoreNotDeleted(@PathVariable Long storeId) {
        return ResponseEntity.ok(service.getByStoreNotDeleted(storeId));
    }

    @GetMapping("/type/{discountType}")
    public ResponseEntity<List<VoucherResponseDto>> getByDiscountType(@PathVariable DiscountType discountType) {
        return ResponseEntity.ok(service.getByDiscountType(discountType));
    }

    @GetMapping("/type/{discountType}/isnot_deleted")
    public ResponseEntity<List<VoucherResponseDto>> getByDiscountTypeNotDeleted(
            @PathVariable DiscountType discountType) {
        return ResponseEntity.ok(service.getByDiscountTypeNotDeleted(discountType));
    }

    @GetMapping("/admin")
    public ResponseEntity<List<VoucherResponseDto>> getAdminVouchers() {
        return ResponseEntity.ok(service.getAdminVouchers());
    }

    @GetMapping("/admin/isnot_deleted")
    public ResponseEntity<List<VoucherResponseDto>> getAdminVouchersNotDeleted() {
        return ResponseEntity.ok(service.getAdminVouchersNotDeleted());
    }

    @GetMapping("/store")
    public ResponseEntity<List<VoucherResponseDto>> getStoreVouchers() {
        return ResponseEntity.ok(service.getStoreVouchers());
    }

    @GetMapping("/store/isnot_deleted")
    public ResponseEntity<List<VoucherResponseDto>> getStoreVouchersNotDeleted() {
        return ResponseEntity.ok(service.getStoreVouchersNotDeleted());
    }

    /**
     * Validate voucher bằng code - Kiểm tra hợp lệ, hết hạn, usage
     */
    @GetMapping("/validate/{code}")
    public ResponseEntity<VoucherResponseDto> validateVoucher(@PathVariable String code) {
        return ResponseEntity.ok(service.validateVoucher(code));
    }

    /**
     * Tăng lượt sử dụng - Gọi khi user apply voucher
     */
    @PostMapping("/{id}/increment-usage")
    public ResponseEntity<VoucherResponseDto> incrementUsage(@PathVariable Long id) {
        return ResponseEntity.ok(service.incrementUsage(id));
    }

    /**
     * Giảm lượt sử dụng - Gọi khi user hủy đơn hoặc rollback
     */
    @PostMapping("/{id}/decrement-usage")
    public ResponseEntity<VoucherResponseDto> decrementUsage(@PathVariable Long id) {
        return ResponseEntity.ok(service.decrementUsage(id));
    }

    /**
     * Kiểm tra voucher đã hết hạn chưa
     */
    @GetMapping("/{id}/is-expired")
    public ResponseEntity<Boolean> isExpired(@PathVariable Long id) {
        return ResponseEntity.ok(service.isVoucherExpired(id));
    }

    /**
     * Kiểm tra voucher đã hết lượt sử dụng chưa
     */
    @GetMapping("/{id}/is-usage-limit-reached")
    public ResponseEntity<Boolean> isUsageLimitReached(@PathVariable Long id) {
        return ResponseEntity.ok(service.isVoucherUsageLimitReached(id));
    }
}
