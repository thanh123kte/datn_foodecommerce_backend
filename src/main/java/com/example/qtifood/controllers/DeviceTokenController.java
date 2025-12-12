package com.example.qtifood.controllers;

import com.example.qtifood.dtos.DeviceTokens.DeviceTokenRequestDto;
import com.example.qtifood.dtos.DeviceTokens.DeviceTokenResponseDto;
import com.example.qtifood.services.DeviceTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-tokens")
@RequiredArgsConstructor
public class DeviceTokenController {
    
    private final DeviceTokenService deviceTokenService;
    
    /**
     * Register or update a device token for current user
     */
    @PostMapping
    public ResponseEntity<DeviceTokenResponseDto> registerDeviceToken(
            @RequestHeader("X-User-ID") String userId,
            @Valid @RequestBody DeviceTokenRequestDto request) {
        DeviceTokenResponseDto response = deviceTokenService.registerDeviceToken(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get all device tokens for current user
     */
    @GetMapping
    public ResponseEntity<List<DeviceTokenResponseDto>> getDeviceTokens(
            @RequestHeader("X-User-ID") String userId) {
        List<DeviceTokenResponseDto> tokens = deviceTokenService.getDeviceTokens(userId);
        return ResponseEntity.ok(tokens);
    }
    
    /**
     * Get device tokens for current user by role
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<DeviceTokenResponseDto>> getDeviceTokensByRole(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable String role) {
        List<DeviceTokenResponseDto> tokens = deviceTokenService.getDeviceTokensByRole(userId, role);
        return ResponseEntity.ok(tokens);
    }
    
    /**
     * Remove a specific device token
     */
    @DeleteMapping
    public ResponseEntity<Void> removeDeviceToken(
            @RequestHeader("X-User-ID") String userId,
            @RequestParam String token) {
        deviceTokenService.removeDeviceToken(userId, token);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Remove all device tokens for current user
     */
    @DeleteMapping("/all")
    public ResponseEntity<Void> removeAllDeviceTokens(
            @RequestHeader("X-User-ID") String userId) {
        deviceTokenService.removeAllDeviceTokens(userId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get count of registered device tokens
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getDeviceTokenCount(
            @RequestHeader("X-User-ID") String userId) {
        long count = deviceTokenService.getDeviceTokenCount(userId);
        return ResponseEntity.ok(count);
    }
}
