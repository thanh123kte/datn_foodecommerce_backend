package com.example.qtifood.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.qtifood.dtos.Drivers.CreateDriverDto;
import com.example.qtifood.dtos.Drivers.UpdateDriverDto;
import com.example.qtifood.dtos.Drivers.DriverResponseDto;
import com.example.qtifood.enums.VerificationStatus;
import com.example.qtifood.services.DriverService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Validated
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<DriverResponseDto> createDriver(@Valid @RequestBody CreateDriverDto dto) {
        DriverResponseDto driver = driverService.createDriver(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(driver);
    }

    @GetMapping
    public ResponseEntity<List<DriverResponseDto>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDto> getDriverById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<DriverResponseDto> getDriverByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(driverService.getDriverByPhone(phone));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDto> updateDriver(@PathVariable Long id,
                                                         @Valid @RequestBody UpdateDriverDto dto) {
        return ResponseEntity.ok(driverService.updateDriver(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verification-status/{status}")
    public ResponseEntity<List<DriverResponseDto>> getDriversByVerificationStatus(
            @PathVariable VerificationStatus status) {
        return ResponseEntity.ok(driverService.getDriversByVerificationStatus(status));
    }

    @GetMapping("/verified/{verified}")
    public ResponseEntity<List<DriverResponseDto>> getVerifiedDrivers(@PathVariable Boolean verified) {
        return ResponseEntity.ok(driverService.getVerifiedDrivers(verified));
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<DriverResponseDto>> searchDriversByName(@RequestParam("q") String name) {
        return ResponseEntity.ok(driverService.searchDriversByName(name));
    }

    @GetMapping("/search/phone")
    public ResponseEntity<List<DriverResponseDto>> searchDriversByPhone(@RequestParam("q") String phone) {
        return ResponseEntity.ok(driverService.searchDriversByPhone(phone));
    }

    @GetMapping("/vehicle-type/{vehicleType}")
    public ResponseEntity<List<DriverResponseDto>> getDriversByVehicleType(@PathVariable String vehicleType) {
        return ResponseEntity.ok(driverService.getDriversByVehicleType(vehicleType));
    }

    @PutMapping("/{id}/verification-status/{status}")
    public ResponseEntity<DriverResponseDto> updateVerificationStatus(@PathVariable Long id,
                                                                     @PathVariable VerificationStatus status) {
        return ResponseEntity.ok(driverService.updateVerificationStatus(id, status));
    }

    @PutMapping("/{id}/verify/{verified}")
    public ResponseEntity<DriverResponseDto> verifyDriver(@PathVariable Long id,
                                                         @PathVariable Boolean verified) {
        return ResponseEntity.ok(driverService.verifyDriver(id, verified));
    }

    @PostMapping(value = "/{id}/avatar", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DriverResponseDto> uploadAvatar(@PathVariable Long id,
                                                         @RequestParam("avatar") org.springframework.web.multipart.MultipartFile avatarFile) {
        return ResponseEntity.ok(driverService.uploadAvatar(id, avatarFile));
    }

    @DeleteMapping("/{id}/avatar")
    public ResponseEntity<DriverResponseDto> deleteAvatar(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.deleteAvatar(id));
    }
}