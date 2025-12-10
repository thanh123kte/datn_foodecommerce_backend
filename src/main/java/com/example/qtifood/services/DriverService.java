package com.example.qtifood.services;

import java.util.List;

import com.example.qtifood.dtos.Drivers.CreateDriverDto;
import com.example.qtifood.dtos.Drivers.UpdateDriverDto;
import com.example.qtifood.dtos.Drivers.DriverResponseDto;
import com.example.qtifood.enums.VerificationStatus;

public interface DriverService {
    
    DriverResponseDto createDriver(CreateDriverDto dto);
    
    List<DriverResponseDto> getAllDrivers();
    
    DriverResponseDto getDriverById(Long id);
    
    DriverResponseDto getDriverByPhone(String phone);
    
    DriverResponseDto updateDriver(Long id, UpdateDriverDto dto);
    
    void deleteDriver(Long id);
    
    List<DriverResponseDto> getDriversByVerificationStatus(VerificationStatus verificationStatus);
    
    List<DriverResponseDto> getVerifiedDrivers(Boolean verified);
    
    List<DriverResponseDto> searchDriversByName(String name);
    
    List<DriverResponseDto> searchDriversByPhone(String phone);
    
    List<DriverResponseDto> getDriversByVehicleType(String vehicleType);
    
    DriverResponseDto updateVerificationStatus(Long id, VerificationStatus verificationStatus);
    
    DriverResponseDto verifyDriver(Long id, Boolean verified);
    
    DriverResponseDto uploadAvatar(Long id, org.springframework.web.multipart.MultipartFile avatarFile);
    
    DriverResponseDto deleteAvatar(Long id);
}