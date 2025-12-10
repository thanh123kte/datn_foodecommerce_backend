package com.example.qtifood.dtos.Drivers;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.qtifood.enums.VerificationStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverResponseDto {
    private String id;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private LocalDate dateOfBirth;
    private String address;
    private String vehicleType;
    private String vehiclePlate;
    private String vehiclePlateImageUrl;
    private String vehicleRegistrationImageUrl;
    private String cccdNumber;
    private String cccdFrontImageUrl;
    private String cccdBackImageUrl;
    private String licenseNumber;
    private String licenseImageUrl;
    private Boolean verified;
    private VerificationStatus verificationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}