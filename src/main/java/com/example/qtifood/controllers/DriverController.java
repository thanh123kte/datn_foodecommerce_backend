package com.example.qtifood.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.qtifood.dtos.Drivers.CreateDriverDto;
import com.example.qtifood.dtos.Drivers.UpdateDriverDto;
import com.example.qtifood.dtos.Drivers.DriverResponseDto;
import com.example.qtifood.enums.DriverStatus;
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
    public ResponseEntity<DriverResponseDto> getDriverById(@PathVariable String id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @GetMapping("/phone/{phone}")
    public ResponseEntity<DriverResponseDto> getDriverByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(driverService.getDriverByPhone(phone));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDto> updateDriver(@PathVariable String id,
                                                         @Valid @RequestBody UpdateDriverDto dto) {
        return ResponseEntity.ok(driverService.updateDriver(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable String id) {
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

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DriverResponseDto>> getDriversByStatus(@PathVariable DriverStatus status) {
        return ResponseEntity.ok(driverService.getDriversByStatus(status));
    }

    @PutMapping("/{id}/verification-status/{status}")
    public ResponseEntity<DriverResponseDto> updateVerificationStatus(@PathVariable String id,
                                                                     @PathVariable VerificationStatus status) {
        return ResponseEntity.ok(driverService.updateVerificationStatus(id, status));
    }

    @PutMapping("/{id}/verify/{verified}")
    public ResponseEntity<DriverResponseDto> verifyDriver(@PathVariable String id,
                                                         @PathVariable Boolean verified) {
        return ResponseEntity.ok(driverService.verifyDriver(id, verified));
    }

    @PostMapping("/{id}/upload-image")
    public ResponseEntity<Map<String, String>> uploadDriverImage(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("imageType") String imageType) {
        String imageUrl = driverService.uploadDriverImage(id, file, imageType);
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DriverResponseDto> updateDriverStatus(
            @PathVariable String id,
            @RequestParam DriverStatus status) {
        UpdateDriverDto dto = new UpdateDriverDto(
                null, null, null, null, null, null, null, 
                null, null, null, null, null, null, null, 
                null, null, status
        );
        return ResponseEntity.ok(driverService.updateDriver(id, dto));
    }
}