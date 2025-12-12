package com.example.qtifood.controllers;

import com.example.qtifood.dtos.Voucher.*;
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

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<VoucherResponseDto>> getBySeller(@PathVariable Long sellerId) {
        return ResponseEntity.ok(service.getBySeller(sellerId));
    }
}
