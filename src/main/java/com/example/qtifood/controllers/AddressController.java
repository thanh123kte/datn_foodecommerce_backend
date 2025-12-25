package com.example.qtifood.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.qtifood.dtos.Addresses.AddressResponseDto;
import com.example.qtifood.dtos.Addresses.CreateAddressDto;
import com.example.qtifood.dtos.Addresses.UpdateAddressDto;
import com.example.qtifood.services.AddressService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Validated
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponseDto> create(@Valid @RequestBody CreateAddressDto dto) {
        return ResponseEntity.ok(addressService.createAddress(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDto> update(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateAddressDto dto) {
        return ResponseEntity.ok(addressService.updateAddress(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/soft-delete")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        addressService.softDeleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getAll() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }

    @GetMapping("/isnot_deleted")
    public ResponseEntity<List<AddressResponseDto>> getAllNotDeleted() {
        return ResponseEntity.ok(addressService.getAllAddressesNotDeleted());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressResponseDto>> getByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(addressService.getAddressesByUserId(userId));
    }

    @GetMapping("/user/{userId}/isnot_deleted")
    public ResponseEntity<List<AddressResponseDto>> getByUserIdNotDeleted(@PathVariable String userId) {
        return ResponseEntity.ok(addressService.getAddressesByUserIdNotDeleted(userId));
    }

    @PutMapping("/{id}/set-default")
    public ResponseEntity<AddressResponseDto> setDefault(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.setDefaultAddress(id));
    }

    @PutMapping("/{id}/set-undefault")
    public ResponseEntity<AddressResponseDto> setUnDefault(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.setUnDefaultAddress(id));
    }
}
