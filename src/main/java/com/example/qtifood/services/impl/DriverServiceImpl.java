package com.example.qtifood.services.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.qtifood.dtos.Drivers.CreateDriverDto;
import com.example.qtifood.dtos.Drivers.UpdateDriverDto;
import com.example.qtifood.dtos.Drivers.DriverResponseDto;
import com.example.qtifood.entities.Driver;
import com.example.qtifood.entities.Role;
import com.example.qtifood.entities.User;
import com.example.qtifood.entities.Wallet;
import com.example.qtifood.enums.RoleType;
import com.example.qtifood.enums.VerificationStatus;
import com.example.qtifood.exceptions.EntityDuplicateException;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.repositories.DriverRepository;
import com.example.qtifood.repositories.RoleRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.repositories.WalletRepository;
import com.example.qtifood.services.DriverService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final WalletRepository walletRepository;

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
                .id(dto.id())
                .fullName(dto.fullName())
                .phone(dto.phone())
                .avatarUrl(dto.avatarUrl())
                .dateOfBirth(dto.dateOfBirth())
                .address(dto.address())
                .vehicleType(dto.vehicleType())
                .vehiclePlate(dto.vehiclePlate())
                .vehiclePlateImageUrl(dto.vehiclePlateImageUrl())
                .vehicleRegistrationImageUrl(dto.vehicleRegistrationImageUrl())
                .cccdNumber(dto.cccdNumber())
                .cccdFrontImageUrl(dto.cccdFrontImageUrl())
                .cccdBackImageUrl(dto.cccdBackImageUrl())
                .licenseNumber(dto.licenseNumber())
                .licenseImageUrl(dto.licenseImageUrl())
                .verificationStatus(dto.verificationStatus() != null ? dto.verificationStatus() : VerificationStatus.PENDING)
                .verified(false)
                .build();

        Driver savedDriver = driverRepository.save(driver);

        // Lấy role DRIVER từ database
        Role driverRole = roleRepository.findByName(RoleType.DRIVER)
                .orElseThrow(() -> new ResourceNotFoundException("Role DRIVER not found"));

        // Tạo User với role DRIVER
        Set<Role> roles = new HashSet<>();
        roles.add(driverRole);

        User user = User.builder()
                .id(savedDriver.getId())
                .fullName(savedDriver.getFullName())
                .phone(savedDriver.getPhone())
                .password("") // Password để trống, driver sẽ đăng nhập qua Firebase
                .isActive(true)
                .roles(roles)
                .build();
        User savedUser = userRepository.save(user);

        // Tạo ví cho tài xế
        Wallet wallet = Wallet.builder()
                .user(savedUser)
                .balance(BigDecimal.ZERO)
                .build();
        walletRepository.save(wallet);

        return toDto(savedDriver);
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
    public DriverResponseDto getDriverById(String id) {
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
    public DriverResponseDto updateDriver(String id, UpdateDriverDto dto) {
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

        if (dto.avatarUrl() != null) {
            driver.setAvatarUrl(dto.avatarUrl());
        }

        if (dto.dateOfBirth() != null) {
            driver.setDateOfBirth(dto.dateOfBirth());
        }

        if (dto.address() != null) {
            driver.setAddress(dto.address());
        }

        if (dto.vehicleType() != null) {
            driver.setVehicleType(dto.vehicleType());
        }

        if (dto.vehiclePlate() != null) {
            driver.setVehiclePlate(dto.vehiclePlate());
        }

        if (dto.vehiclePlateImageUrl() != null) {
            driver.setVehiclePlateImageUrl(dto.vehiclePlateImageUrl());
        }

        if (dto.vehicleRegistrationImageUrl() != null) {
            driver.setVehicleRegistrationImageUrl(dto.vehicleRegistrationImageUrl());
        }

        if (dto.cccdFrontImageUrl() != null) {
            driver.setCccdFrontImageUrl(dto.cccdFrontImageUrl());
        }

        if (dto.cccdBackImageUrl() != null) {
            driver.setCccdBackImageUrl(dto.cccdBackImageUrl());
        }

        if (dto.licenseImageUrl() != null) {
            driver.setLicenseImageUrl(dto.licenseImageUrl());
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
    public void deleteDriver(String id) {
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
    public DriverResponseDto updateVerificationStatus(String id, VerificationStatus verificationStatus) {
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
    public DriverResponseDto verifyDriver(String id, Boolean verified) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
        
        // Only allow verification change if verificationStatus is APPROVED
        if (driver.getVerificationStatus() != VerificationStatus.APPROVED) {
            throw new IllegalStateException(
                "Cannot change verified status. Driver verification status must be APPROVED first. " +
                "Current status: " + driver.getVerificationStatus()
            );
        }
        
        driver.setVerified(verified);
        
        return toDto(driverRepository.save(driver));
    }

    @Override
    public String uploadDriverImage(String id, MultipartFile file, String imageType) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File phải là ảnh (jpg, png, gif, etc.)");
        }

        // Get current image URL based on type to delete old file
        String oldImageUrl = null;
        switch (imageType.toLowerCase()) {
            case "avatar":
                oldImageUrl = driver.getAvatarUrl();
                break;
            case "cccd_front":
                oldImageUrl = driver.getCccdFrontImageUrl();
                break;
            case "cccd_back":
                oldImageUrl = driver.getCccdBackImageUrl();
                break;
            case "license":
                oldImageUrl = driver.getLicenseImageUrl();
                break;
            case "vehicle_plate":
                oldImageUrl = driver.getVehiclePlateImageUrl();
                break;
            case "vehicle_registration":
                oldImageUrl = driver.getVehicleRegistrationImageUrl();
                break;
            default:
                throw new IllegalArgumentException("Invalid image type: " + imageType);
        }

        // Delete old image file if exists
        if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
            try {
                // Remove leading slash if present
                String oldFilePath = oldImageUrl.startsWith("/") ? oldImageUrl.substring(1) : oldImageUrl;
                Path oldPath = Paths.get(oldFilePath);
                Files.deleteIfExists(oldPath);
            } catch (IOException e) {
                // Log error but continue with upload
                System.err.println("Could not delete old image: " + oldImageUrl + " - " + e.getMessage());
            }
        }

        // Create upload directory
        String uploadDir = "uploads/drivers/";
        java.io.File directory = new java.io.File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String uniqueFilename = UUID.randomUUID().toString() + "_" + 
                imageType + "_" + id + fileExtension;

            // Save file
            Path filePath = Paths.get(uploadDir + uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Generate URL
            String imageUrl = "/" + uploadDir + uniqueFilename;

            // Update driver based on image type
            switch (imageType.toLowerCase()) {
                case "avatar":
                    driver.setAvatarUrl(imageUrl);
                    break;
                case "cccd_front":
                    driver.setCccdFrontImageUrl(imageUrl);
                    break;
                case "cccd_back":
                    driver.setCccdBackImageUrl(imageUrl);
                    break;
                case "license":
                    driver.setLicenseImageUrl(imageUrl);
                    break;
                case "vehicle_plate":
                    driver.setVehiclePlateImageUrl(imageUrl);
                    break;
                case "vehicle_registration":
                    driver.setVehicleRegistrationImageUrl(imageUrl);
                    break;
            }

            driverRepository.save(driver);
            return imageUrl;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file: " + file.getOriginalFilename(), e);
        }
    }

    private DriverResponseDto toDto(Driver driver) {
        return DriverResponseDto.builder()
                .id(driver.getId())
                .fullName(driver.getFullName())
                .phone(driver.getPhone())
                .avatarUrl(driver.getAvatarUrl())
                .dateOfBirth(driver.getDateOfBirth())
                .address(driver.getAddress())
                .vehicleType(driver.getVehicleType())
                .vehiclePlate(driver.getVehiclePlate())
                .vehiclePlateImageUrl(driver.getVehiclePlateImageUrl())
                .vehicleRegistrationImageUrl(driver.getVehicleRegistrationImageUrl())
                .cccdNumber(driver.getCccdNumber())
                .cccdFrontImageUrl(driver.getCccdFrontImageUrl())
                .cccdBackImageUrl(driver.getCccdBackImageUrl())
                .licenseNumber(driver.getLicenseNumber())
                .licenseImageUrl(driver.getLicenseImageUrl())
                .verified(driver.getVerified())
                .verificationStatus(driver.getVerificationStatus())
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }
}