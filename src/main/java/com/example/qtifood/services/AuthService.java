package com.example.qtifood.services;

import com.example.qtifood.dtos.auth.GoogleLoginRequest;
import com.example.qtifood.dtos.auth.FirebaseLoginRequest;
import com.example.qtifood.dtos.auth.LoginResponse;
import com.example.qtifood.entities.Role;
import com.example.qtifood.entities.User;
import com.example.qtifood.enums.RoleType;
import com.example.qtifood.exceptions.BadRequestException;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final GoogleTokenValidationService googleTokenValidationService;
    
    public LoginResponse googleLogin(GoogleLoginRequest request) {
        // Validate Google token and get user info
        GoogleUserInfo googleUserInfo = googleTokenValidationService.validateToken(request.getGoogleToken());
        
        // Check if user exists
        User user = userRepository.findByEmail(googleUserInfo.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found. Please contact admin to create your seller account."));
        
        if (!user.getIsActive()) {
            throw new BadRequestException("Account is deactivated");
        }
        
        // Check if user has SELLER role
        boolean hasSeller = user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleType.SELLER);
        
        if (!hasSeller) {
            throw new BadRequestException("Access denied. Only users with SELLER role can login to seller portal.");
        }
        
        String role = user.getRoles().stream()
                .filter(r -> r.getName() == RoleType.SELLER)
                .findFirst()
                .map(Role::getName)
                .map(RoleType::name)
                .orElse(RoleType.SELLER.name());
        
        String token = jwtUtils.generateToken(user.getEmail(), user.getId(), role);
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                role
        );
        
        return new LoginResponse(token, userInfo);
    }
    
    public LoginResponse firebaseLogin(FirebaseLoginRequest request) {
        // Here we should validate Firebase ID token, but for simplicity, we'll extract email from token
        // In production, use Firebase Admin SDK to verify the token
        try {
            // For now, we'll decode the token to get email (this is not secure, just for demo)
            // In production, use Firebase Admin SDK: FirebaseAuth.getInstance().verifyIdToken(request.getIdToken())
            String email = extractEmailFromIdToken(request.getIdToken());
            
            // Check if user exists
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BadRequestException("User not found. Please contact admin to create your account."));
            
            if (!user.getIsActive()) {
                throw new BadRequestException("Account is deactivated");
            }
            
            // Determine required role from request
            RoleType requiredRoleType;
            try {
                requiredRoleType = RoleType.valueOf(request.getRequiredRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid role type");
            }
            
            // Check if user has the required role
            boolean hasRequiredRole = user.getRoles().stream()
                    .anyMatch(role -> role.getName() == requiredRoleType);
            
            if (!hasRequiredRole) {
                String portalName = requiredRoleType == RoleType.ADMIN ? "admin" : "seller";
                throw new BadRequestException("Access denied. Only users with " + requiredRoleType.name() + " role can login to " + portalName + " portal.");
            }
            
            String role = user.getRoles().stream()
                    .filter(r -> r.getName() == requiredRoleType)
                    .findFirst()
                    .map(Role::getName)
                    .map(RoleType::name)
                    .orElse(requiredRoleType.name());
            
            String token = jwtUtils.generateToken(user.getEmail(), user.getId(), role);
            
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getPhone(),
                    role
            );
            
            return new LoginResponse(token, userInfo);
            
        } catch (Exception e) {
            throw new BadRequestException("Invalid Firebase ID token");
        }
    }
    
    private String extractEmailFromIdToken(String idToken) {
        // Simplified JWT decode - extracts email from Firebase ID token payload
        try {
            String[] parts = idToken.split("\\.");
            if (parts.length != 3) {
                throw new BadRequestException("Invalid token format");
            }
            
            // Decode the payload (2nd part) from Base64URL
            String payload = parts[1];
            
            // Decode Base64URL (replace characters and add padding)
            payload = payload.replace('-', '+').replace('_', '/');
            while (payload.length() % 4 != 0) {
                payload += "=";
            }
            
            // Decode and parse JSON
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(payload);
            String decodedPayload = new String(decodedBytes, java.nio.charset.StandardCharsets.UTF_8);
            
            // Extract email using simple JSON parsing
            // Look for "email":"xxx@xxx.com" pattern
            int emailStart = decodedPayload.indexOf("\"email\":\"");
            if (emailStart == -1) {
                throw new BadRequestException("Email not found in token");
            }
            
            emailStart += 9; // Skip past "email":"
            int emailEnd = decodedPayload.indexOf("\"", emailStart);
            if (emailEnd == -1) {
                throw new BadRequestException("Invalid email format in token");
            }
            
            String email = decodedPayload.substring(emailStart, emailEnd);
            
            if (email == null || email.isEmpty()) {
                throw new BadRequestException("Empty email in token");
            }
            
            return email;
            
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Failed to extract email from token: " + e.getMessage());
        }
    }
    
    
    // Inner class for Google user info
    public static class GoogleUserInfo {
        private String email;
        private String name;
        private String picture;
        
        public GoogleUserInfo(String email, String name, String picture) {
            this.email = email;
            this.name = name;
            this.picture = picture;
        }
        
        // Getters
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getPicture() { return picture; }
    }
}