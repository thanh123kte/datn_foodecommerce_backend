package com.example.qtifood.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleLoginRequest {
    @NotBlank(message = "Google token is required")
    private String googleToken;
}
