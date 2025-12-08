package com.example.qtifood.controllers;

import com.example.qtifood.dtos.auth.GoogleLoginRequest;
import com.example.qtifood.dtos.auth.FirebaseLoginRequest;
import com.example.qtifood.dtos.auth.LoginResponse;
import com.example.qtifood.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/google")
    @Operation(summary = "Login with Google token (fallback - use /api/users for registration)")
    public ResponseEntity<LoginResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest googleLoginRequest) {
        LoginResponse response = authService.googleLogin(googleLoginRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/firebase")
    @Operation(summary = "Login with Firebase ID token (seller portal)")
    public ResponseEntity<LoginResponse> firebaseLogin(@Valid @RequestBody FirebaseLoginRequest firebaseLoginRequest) {
        LoginResponse response = authService.firebaseLogin(firebaseLoginRequest);
        return ResponseEntity.ok(response);
    }
}
