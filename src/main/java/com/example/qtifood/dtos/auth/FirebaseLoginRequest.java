package com.example.qtifood.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FirebaseLoginRequest {
    @NotBlank(message = "Firebase ID token is required")
    private String idToken;
    
    @NotBlank(message = "Required role is required")
    private String requiredRole; // "ADMIN" or "SELLER"
}
