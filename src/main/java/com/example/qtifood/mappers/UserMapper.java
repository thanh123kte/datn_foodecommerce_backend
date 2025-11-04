package com.example.qtifood.mappers;

import com.example.qtifood.dtos.user.UserResponseDto;
import com.example.qtifood.entities.User;
import com.example.qtifood.entities.RoleType;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponseDto toDto(User user) {
        if (user == null) {
            return null;
        }

        // Lấy roles từ user (giả sử user có relationship với roles)
        Set<RoleType> roles = user.getRoles() != null ? 
            user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet()) : 
            Set.of();

        return new UserResponseDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatarUrl(),
                user.getDateOfBirth(),
                user.getGender(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                roles
        );
    }
}