// src/main/java/com/example/qtifood/dtos/user/CreateUserRequestDto.java
package com.example.qtifood.dtos.user;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

import com.example.qtifood.enums.RoleType;

public record CreateUserRequestDto(
        @NotBlank String id,
        @NotBlank @Size(max = 100) String fullName,
        @Email @Size(max = 100) String email,
        @Size(max = 20) String phone,
        @NotBlank @Size(min = 6, max = 255) String password,
        String avatarUrl,
        LocalDate dateOfBirth,
        @Size(max = 10) String gender,
        Boolean isActive,
        // danh sách role truyền vào, ví dụ ["ADMIN", "SELLER"]; nếu null/empty sẽ mặc định CUSTOMER
        Set<RoleType> roles
) {}
