package com.example.qtifood.mappers;

import org.springframework.stereotype.Component;

import com.example.qtifood.dtos.Drivers.DriverResponseDto;
import com.example.qtifood.entities.Driver;

@Component
public class DriverMapper {

    public DriverResponseDto toDto(Driver driver) {
        if (driver == null) {
            return null;
        }

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