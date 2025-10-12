// src/main/java/com/example/qtifood/dtos/user/UpdateUserRequestDto.java
package com.example.qtifood.dtos.user;

import com.example.qtifood.entities.RoleType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

public record UpdateUserRequestDto(
        @Size(max = 100) String fullName,
        @Email @Size(max = 100) String email,
        @Size(max = 20) String phone,
        @Size(min = 6, max = 255) String password,
        String avatarUrl,
        LocalDate dateOfBirth,
        @Size(max = 10) String gender,
        Boolean isActive,
        // nếu cung cấp, sẽ "ghi đè" toàn bộ roles của user bằng danh sách mới
        Set<RoleType> roles
) {}
