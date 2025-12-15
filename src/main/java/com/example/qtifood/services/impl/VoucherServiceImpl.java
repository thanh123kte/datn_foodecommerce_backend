package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.Voucher.*;
import com.example.qtifood.entities.Voucher;
import com.example.qtifood.entities.Store;
import com.example.qtifood.repositories.StoreRepository;
import com.example.qtifood.repositories.VoucherRepository;
import com.example.qtifood.services.VoucherService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VoucherServiceImpl implements VoucherService {

    private static final Logger log = LoggerFactory.getLogger(VoucherServiceImpl.class);
    private final VoucherRepository repo;
    private final StoreRepository storeRepo;

    private VoucherResponseDto toDto(Voucher v) {
        return new VoucherResponseDto(
                v.getId(),
                v.getCode(),
                v.getTitle(),
                v.getDescription(),
                v.getDiscountType(),
                v.getDiscountValue(),
                v.getMinOrderValue(),
                v.getMaxDiscount(),
                v.getStartDate(),
                v.getEndDate(),
                v.getUsageLimit(),
                v.getUsageCount(),
                v.getSeller() != null ? v.getSeller().getId() : null,
                v.getSeller() != null ? v.getSeller().getName() : null,
                v.getStatus(),
                v.getIsActive(),
                v.getIsCreatedByAdmin(),
                v.getCreatedAt(),
                v.getUpdatedAt()
        );
    }

    @Override
    public VoucherResponseDto create(CreateVoucherDto dto) {
        Store seller = null;

        // Nếu có sellerId → đây là voucher của cửa hàng
        if (dto.sellerId() != null) {
            seller = storeRepo.findById(dto.sellerId())
                    .orElseThrow(() -> new IllegalArgumentException("Seller not found: " + dto.sellerId()));
        }

        // Nếu không có sellerId → voucher toàn hệ thống (admin tạo)
        Voucher v = Voucher.builder()
                .code(dto.code())
                .title(dto.title())
                .description(dto.description())
                .discountType(dto.discountType())
                .discountValue(dto.discountValue())
                .minOrderValue(dto.minOrderValue())
                .maxDiscount(dto.maxDiscount())
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .usageLimit(dto.usageLimit())
                .seller(seller) // 👈 có thể null
                .status(dto.status())
                .isActive(dto.isActive())
                .isCreatedByAdmin(dto.isCreatedByAdmin() != null ? dto.isCreatedByAdmin() : false)
                .build();

        return toDto(repo.save(v));
    }


    @Override
    @Transactional(readOnly = true)
    public List<VoucherResponseDto> getAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherResponseDto getById(Long id) {
        return repo.findById(id).map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Voucher not found: " + id));
    }

    @Override
    public VoucherResponseDto update(Long id, UpdateVoucherDto dto) {
        Voucher v = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Voucher not found: " + id));

        if (dto.title() != null) v.setTitle(dto.title());
        if (dto.description() != null) v.setDescription(dto.description());
        if (dto.discountType() != null) v.setDiscountType(dto.discountType());
        if (dto.discountValue() != null) v.setDiscountValue(dto.discountValue());
        if (dto.minOrderValue() != null) v.setMinOrderValue(dto.minOrderValue());
        if (dto.maxDiscount() != null) v.setMaxDiscount(dto.maxDiscount());
        if (dto.startDate() != null) v.setStartDate(dto.startDate());
        if (dto.endDate() != null) v.setEndDate(dto.endDate());
        if (dto.usageLimit() != null) v.setUsageLimit(dto.usageLimit());
        if (dto.status() != null) v.setStatus(dto.status());
        if (dto.isActive() != null) v.setIsActive(dto.isActive());

        return toDto(repo.save(v));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherResponseDto> getBySeller(Long sellerId) {
        return repo.findAllBySeller_Id(sellerId).stream().map(this::toDto).toList();
    }

    @Override
    public VoucherResponseDto incrementUsage(Long voucherId) {
        Voucher voucher = repo.findById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("Voucher not found: " + voucherId));

        // Kiểm tra đã hết hạn chưa
        if (isVoucherExpired(voucherId)) {
            throw new IllegalStateException("Voucher đã hết hạn");
        }

        // Kiểm tra đã đạt limit chưa
        if (isVoucherUsageLimitReached(voucherId)) {
            throw new IllegalStateException("Voucher đã hết lượt sử dụng");
        }

        Integer currentCount = voucher.getUsageCount() != null ? voucher.getUsageCount() : 0;
        voucher.setUsageCount(currentCount + 1);

        log.info("[Voucher] Incremented usage for voucher={}, code={}, count={}",
                voucherId, voucher.getCode(), voucher.getUsageCount());

        return toDto(repo.save(voucher));
    }

    @Override
    public VoucherResponseDto decrementUsage(Long voucherId) {
        Voucher voucher = repo.findById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("Voucher not found: " + voucherId));

        Integer currentCount = voucher.getUsageCount() != null ? voucher.getUsageCount() : 0;
        if (currentCount > 0) {
            voucher.setUsageCount(currentCount - 1);
            log.info("[Voucher] Decremented usage for voucher={}, code={}, count={}",
                    voucherId, voucher.getCode(), voucher.getUsageCount());
        } else {
            log.warn("[Voucher] Cannot decrement usage for voucher={}, already at 0", voucherId);
        }

        return toDto(repo.save(voucher));
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherResponseDto validateVoucher(String code) {
        Voucher voucher = repo.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Voucher không tồn tại: " + code));

        // Kiểm tra active
        if (voucher.getIsActive() == null || !voucher.getIsActive()) {
            throw new IllegalStateException("Voucher không còn khả dụng");
        }

        // Kiểm tra hết hạn
        if (isVoucherExpired(voucher.getId())) {
            throw new IllegalStateException("Voucher đã hết hạn");
        }

        // Kiểm tra usage limit
        if (isVoucherUsageLimitReached(voucher.getId())) {
            throw new IllegalStateException("Voucher đã hết lượt sử dụng");
        }

        log.info("[Voucher] Validated voucher code={}, valid=true", code);
        return toDto(voucher);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVoucherExpired(Long voucherId) {
        Voucher voucher = repo.findById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("Voucher not found: " + voucherId));

        LocalDateTime now = LocalDateTime.now();

        // Check start date
        if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
            log.debug("[Voucher] Voucher={} chưa đến thời gian sử dụng", voucherId);
            return true; // Chưa tới thời gian
        }

        // Check end date
        if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
            log.debug("[Voucher] Voucher={} đã hết hạn", voucherId);
            return true; // Đã hết hạn
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVoucherUsageLimitReached(Long voucherId) {
        Voucher voucher = repo.findById(voucherId)
                .orElseThrow(() -> new IllegalArgumentException("Voucher not found: " + voucherId));

        if (voucher.getUsageLimit() == null) {
            return false; // Không giới hạn
        }

        Integer currentCount = voucher.getUsageCount() != null ? voucher.getUsageCount() : 0;
        boolean limitReached = currentCount >= voucher.getUsageLimit();

        if (limitReached) {
            log.debug("[Voucher] Voucher={} đã đạt usage limit: {}/{}",
                    voucherId, currentCount, voucher.getUsageLimit());
        }

        return limitReached;
    }
}
