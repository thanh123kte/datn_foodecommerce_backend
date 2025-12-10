package com.example.qtifood.dtos.Drivers;

import com.example.qtifood.enums.VerificationStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateDriverDto(
        @NotBlank(message = "Driver ID is required")
        @Size(max = 255, message = "Driver ID must not exceed 255 characters")
        String id,

        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name must not exceed 100 characters")
        String fullName,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must be 10-11 digits")
        String phone,

        String avatarUrl,

        LocalDate dateOfBirth,

        @Size(max = 255, message = "Address must not exceed 255 characters")
        String address,

        @Size(max = 50, message = "Vehicle type must not exceed 50 characters")
        String vehicleType,

        @Size(max = 20, message = "Vehicle plate must not exceed 20 characters")
        String vehiclePlate,

        String vehiclePlateImageUrl,

        String vehicleRegistrationImageUrl,

        @Size(max = 20, message = "CCCD number must not exceed 20 characters")
        String cccdNumber,

        String cccdFrontImageUrl,

        String cccdBackImageUrl,

        @Size(max = 50, message = "License number must not exceed 50 characters")
        String licenseNumber,

        String licenseImageUrl,

        VerificationStatus verificationStatus
) {}