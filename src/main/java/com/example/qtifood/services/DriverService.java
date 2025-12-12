package com.example.qtifood.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.qtifood.dtos.Drivers.CreateDriverDto;
import com.example.qtifood.dtos.Drivers.UpdateDriverDto;
import com.example.qtifood.dtos.Drivers.DriverResponseDto;
import com.example.qtifood.enums.DriverStatus;
import com.example.qtifood.enums.VerificationStatus;

public interface DriverService {
    
    DriverResponseDto createDriver(CreateDriverDto dto);
    
    List<DriverResponseDto> getAllDrivers();
    
    DriverResponseDto getDriverById(String id);
    
    DriverResponseDto getDriverByPhone(String phone);
    
    DriverResponseDto updateDriver(String id, UpdateDriverDto dto);
    
    void deleteDriver(String id);
    
    List<DriverResponseDto> getDriversByVerificationStatus(VerificationStatus verificationStatus);
    
    List<DriverResponseDto> getVerifiedDrivers(Boolean verified);
    
    List<DriverResponseDto> searchDriversByName(String name);
    
    List<DriverResponseDto> searchDriversByPhone(String phone);
    
    List<DriverResponseDto> getDriversByVehicleType(String vehicleType);
    
    DriverResponseDto updateVerificationStatus(String id, VerificationStatus verificationStatus);
    
    DriverResponseDto verifyDriver(String id, Boolean verified);
    
    String uploadDriverImage(String id, MultipartFile file, String imageType);
    
    List<DriverResponseDto> getDriversByStatus(DriverStatus status);
}