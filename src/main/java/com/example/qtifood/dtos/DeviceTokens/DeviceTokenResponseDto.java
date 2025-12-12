package com.example.qtifood.dtos.DeviceTokens;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceTokenResponseDto {
    
    private Long id;
    private String userId;
    private String role;
    private String token;
    private String platform;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
