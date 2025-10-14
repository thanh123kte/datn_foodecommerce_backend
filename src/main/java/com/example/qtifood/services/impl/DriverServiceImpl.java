package com.example.qtifood.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.qtifood.dtos.Drivers.CreateDriverDto;
import com.example.qtifood.dtos.Drivers.UpdateDriverDto;
import com.example.qtifood.dtos.Drivers.DriverResponseDto;
import com.example.qtifood.entities.Driver;
import com.example.qtifood.enums.VerificationStatus;
import com.example.qtifood.exceptions.EntityDuplicateException;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.repositories.DriverRepository;
import com.example.qtifood.services.DriverService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public DriverResponseDto createDriver(CreateDriverDto dto) {
        // Check if phone already exists
        if (driverRepository.existsByPhone(dto.phone())) {
            throw new EntityDuplicateException("Driver with phone '" + dto.phone() + "' already exists");
        }

        // Check if CCCD number already exists (if provided)
        if (dto.cccdNumber() != null && driverRepository.existsByCccdNumber(dto.cccdNumber())) {
            throw new EntityDuplicateException("Driver with CCCD number '" + dto.cccdNumber() + "' already exists");
        }

        // Check if license number already exists (if provided)
        if (dto.licenseNumber() != null && driverRepository.existsByLicenseNumber(dto.licenseNumber())) {
            throw new EntityDuplicateException("Driver with license number '" + dto.licenseNumber() + "' already exists");
        }

        Driver driver = Driver.builder()
                .fullName(dto.fullName())
                .phone(dto.phone())
                .password(passwordEncoder.encode(dto.password()))
                .avatarUrl(dto.avatarUrl())
                .vehicleType(dto.vehicleType())
                .vehiclePlate(dto.vehiclePlate())
                .cccdNumber(dto.cccdNumber())
                .licenseNumber(dto.licenseNumber())
                .verificationStatus(dto.verificationStatus() != null ? dto.verificationStatus() : VerificationStatus.PENDING)
                .verified(false)
                .build();

        return toDto(driverRepository.save(driver));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponseDto> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DriverResponseDto getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        return toDto(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverResponseDto getDriverByPhone(String phone) {
        Driver driver = driverRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with phone: " + phone));
        return toDto(driver);
    }

    @Override
    public DriverResponseDto updateDriver(Long id, UpdateDriverDto dto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        // Check phone uniqueness if updating
        if (dto.phone() != null && !dto.phone().equals(driver.getPhone())) {
            if (driverRepository.existsByPhoneAndIdNot(dto.phone(), id)) {
                throw new EntityDuplicateException("Driver with phone '" + dto.phone() + "' already exists");
            }
            driver.setPhone(dto.phone());
        }

        // Check CCCD number uniqueness if updating
        if (dto.cccdNumber() != null && !dto.cccdNumber().equals(driver.getCccdNumber())) {
            if (driverRepository.existsByCccdNumberAndIdNot(dto.cccdNumber(), id)) {
                throw new EntityDuplicateException("Driver with CCCD number '" + dto.cccdNumber() + "' already exists");
            }
            driver.setCccdNumber(dto.cccdNumber());
        }

        // Check license number uniqueness if updating
        if (dto.licenseNumber() != null && !dto.licenseNumber().equals(driver.getLicenseNumber())) {
            if (driverRepository.existsByLicenseNumberAndIdNot(dto.licenseNumber(), id)) {
                throw new EntityDuplicateException("Driver with license number '" + dto.licenseNumber() + "' already exists");
            }
            driver.setLicenseNumber(dto.licenseNumber());
        }

        // Update other fields
        if (dto.fullName() != null) {
            driver.setFullName(dto.fullName());
        }

        if (dto.password() != null) {
                    // Check if vehicle plate already exists (if provided)
                    if (dto.vehiclePlate() != null && driverRepository.existsByVehiclePlate(dto.vehiclePlate())) {
                        throw new EntityDuplicateException("Vehicle plate already exists");
                    }
            driver.setPassword(passwordEncoder.encode(dto.password()));
        }

        if (dto.avatarUrl() != null) {
            driver.setAvatarUrl(dto.avatarUrl());
        }

        if (dto.vehicleType() != null) {
            driver.setVehicleType(dto.vehicleType());
        }

        if (dto.vehiclePlate() != null) {
            driver.setVehiclePlate(dto.vehiclePlate());
        }

        if (dto.verified() != null) {
            driver.setVerified(dto.verified());
        }

        if (dto.verificationStatus() != null) {
            driver.setVerificationStatus(dto.verificationStatus());
        }

        return toDto(driverRepository.save(driver));
    }

    @Override
    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        driverRepository.delete(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponseDto> getDriversByVerificationStatus(VerificationStatus verificationStatus) {
        return driverRepository.findByVerificationStatus(verificationStatus)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponseDto> getVerifiedDrivers(Boolean verified) {
        return driverRepository.findByVerified(verified)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponseDto> searchDriversByName(String name) {
        return driverRepository.searchByName(name)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponseDto> searchDriversByPhone(String phone) {
        return driverRepository.searchByPhone(phone)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponseDto> getDriversByVehicleType(String vehicleType) {
        return driverRepository.findByVehicleType(vehicleType)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DriverResponseDto updateVerificationStatus(Long id, VerificationStatus verificationStatus) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        
        driver.setVerificationStatus(verificationStatus);
        
        // Auto-verify if approved
        if (verificationStatus == VerificationStatus.APPROVED) {
            driver.setVerified(true);
        } else if (verificationStatus == VerificationStatus.REJECTED) {
            driver.setVerified(false);
        }
        
        return toDto(driverRepository.save(driver));
    }

    @Override
    public DriverResponseDto verifyDriver(Long id, Boolean verified) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        
        driver.setVerified(verified);
        
        // Update verification status accordingly
        if (verified) {
            driver.setVerificationStatus(VerificationStatus.APPROVED);
        } else {
            driver.setVerificationStatus(VerificationStatus.REJECTED);
        }
        
        return toDto(driverRepository.save(driver));
    }

    private DriverResponseDto toDto(Driver driver) {
        return DriverResponseDto.builder()
                .id(driver.getId())
                .fullName(driver.getFullName())
                .phone(driver.getPhone())
                .avatarUrl(driver.getAvatarUrl())
                .vehicleType(driver.getVehicleType())
                .vehiclePlate(driver.getVehiclePlate())
                .cccdNumber(driver.getCccdNumber())
                .licenseNumber(driver.getLicenseNumber())
                .verified(driver.getVerified())
                .verificationStatus(driver.getVerificationStatus())
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }
}