package com.example.qtifood.dtos.Drivers;

import java.time.LocalDateTime;

import com.example.qtifood.enums.VerificationStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverResponseDto {
    private Long id;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private String vehicleType;
    private String vehiclePlate;
    private String cccdNumber;
    private String licenseNumber;
    private Boolean verified;
    private VerificationStatus verificationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}