// src/main/java/com/example/qtifood/dtos/user/UserResponseDto.java
package com.example.qtifood.dtos.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.example.qtifood.enums.RoleType;


public record UserResponseDto(
        String id,
        String fullName,
        String email,
        String phone,
        String avatarUrl,
        LocalDate dateOfBirth,
        String gender,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Set<RoleType> roles
) {}
