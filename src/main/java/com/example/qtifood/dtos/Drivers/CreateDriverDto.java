package com.example.qtifood.dtos.Drivers;

import com.example.qtifood.enums.VerificationStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateDriverDto(
        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name must not exceed 100 characters")
        String fullName,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must be 10-11 digits")
        String phone,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
        String password,

        String avatarUrl,

        @Size(max = 50, message = "Vehicle type must not exceed 50 characters")
        String vehicleType,

        @Size(max = 20, message = "Vehicle plate must not exceed 20 characters")
        String vehiclePlate,

        @Size(max = 20, message = "CCCD number must not exceed 20 characters")
        String cccdNumber,

        @Size(max = 50, message = "License number must not exceed 50 characters")
        String licenseNumber,

        VerificationStatus verificationStatus
) {}