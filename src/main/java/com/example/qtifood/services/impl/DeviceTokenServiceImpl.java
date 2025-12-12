package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.DeviceTokens.DeviceTokenRequestDto;
import com.example.qtifood.dtos.DeviceTokens.DeviceTokenResponseDto;
import com.example.qtifood.entities.DeviceToken;
import com.example.qtifood.entities.User;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.repositories.DeviceTokenRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.services.DeviceTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceTokenServiceImpl implements DeviceTokenService {
    
    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;
    
    @Override
    public DeviceTokenResponseDto registerDeviceToken(String userId, DeviceTokenRequestDto request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        
        // Check if token already exists for this user
        DeviceToken existingToken = deviceTokenRepository.findByUserIdAndToken(userId, request.getToken())
            .orElse(null);
        
        DeviceToken deviceToken;
        if (existingToken != null) {
            // Update existing token
            existingToken.setPlatform(request.getPlatform());
            deviceToken = deviceTokenRepository.save(existingToken);
        } else {
            // Determine role name (ADMIN, CUSTOMER, SELLER)
            String roleName = "CUSTOMER";
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                com.example.qtifood.enums.RoleType rt = user.getRoles().iterator().next().getName();
                roleName = rt.name();
            }
            deviceToken = DeviceToken.builder()
                .user(user)
                .userId(userId)
                .role(roleName)
                .token(request.getToken())
                .platform(request.getPlatform())
                .build();
            deviceToken = deviceTokenRepository.save(deviceToken);
        }
        
        return toDto(deviceToken);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DeviceTokenResponseDto> getDeviceTokens(String userId) {
        return deviceTokenRepository.findByUserId(userId).stream()
            .map(this::toDto)
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DeviceTokenResponseDto> getDeviceTokensByRole(String userId, String role) {
        return deviceTokenRepository.findByUserIdAndRole(userId, role).stream()
            .map(this::toDto)
            .toList();
    }
    
    @Override
    public void removeDeviceToken(String userId, String token) {
        DeviceToken deviceToken = deviceTokenRepository.findByUserIdAndToken(userId, token)
            .orElseThrow(() -> new ResourceNotFoundException("Device token not found"));
        deviceTokenRepository.delete(deviceToken);
    }
    
    @Override
    public void removeAllDeviceTokens(String userId) {
        deviceTokenRepository.deleteByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DeviceTokenResponseDto> getDeviceTokensByUserIdsAndRole(List<String> userIds, String role) {
        return deviceTokenRepository.findByUserIdsAndRole(userIds, role).stream()
            .map(this::toDto)
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getDeviceTokenCount(String userId) {
        return deviceTokenRepository.countByUserId(userId);
    }
    
    private DeviceTokenResponseDto toDto(DeviceToken deviceToken) {
        return DeviceTokenResponseDto.builder()
            .id(deviceToken.getId())
            .userId(deviceToken.getUserId())
            .role(deviceToken.getRole())
            .token(deviceToken.getToken())
            .platform(deviceToken.getPlatform())
            .createdAt(deviceToken.getCreatedAt())
            .updatedAt(deviceToken.getUpdatedAt())
            .build();
    }
}
