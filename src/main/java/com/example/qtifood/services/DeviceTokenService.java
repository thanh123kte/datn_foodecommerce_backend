package com.example.qtifood.services;

import com.example.qtifood.dtos.DeviceTokens.DeviceTokenRequestDto;
import com.example.qtifood.dtos.DeviceTokens.DeviceTokenResponseDto;

import java.util.List;

public interface DeviceTokenService {
    
    /**
     * Register or update a device token for a user
     */
    DeviceTokenResponseDto registerDeviceToken(String userId, DeviceTokenRequestDto request);
    
    /**
     * Get all device tokens for a user
     */
    List<DeviceTokenResponseDto> getDeviceTokens(String userId);
    
    /**
     * Get device tokens for a user by role
     */
    List<DeviceTokenResponseDto> getDeviceTokensByRole(String userId, String role);
    
    /**
     * Remove a device token
     */
    void removeDeviceToken(String userId, String token);
    
    /**
     * Remove all device tokens for a user
     */
    void removeAllDeviceTokens(String userId);
    
    /**
     * Get device tokens for multiple users (for notification broadcasting)
     */
    List<DeviceTokenResponseDto> getDeviceTokensByUserIdsAndRole(List<String> userIds, String role);
    
    /**
     * Get count of device tokens for a user
     */
    long getDeviceTokenCount(String userId);
}
