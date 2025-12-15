package com.example.qtifood.repositories;

import com.example.qtifood.entities.Voucher;
import com.example.qtifood.enums.DiscountType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);
    List<Voucher> findAllBySeller_Id(Long sellerId);
    List<Voucher> findAllByDiscountType(DiscountType discountType);
    List<Voucher> findAllByIsCreatedByAdmin(Boolean isCreatedByAdmin);
}
