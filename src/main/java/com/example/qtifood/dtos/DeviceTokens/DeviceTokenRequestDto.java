package com.example.qtifood.dtos.DeviceTokens;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceTokenRequestDto {
    
    @NotBlank(message = "Token is required")
    private String token;
    
    @NotBlank(message = "Platform is required (iOS, Android, Web)")
    private String platform;
}
