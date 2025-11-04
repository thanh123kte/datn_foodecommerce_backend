// src/main/java/com/example/qtifood/services/impl/UserServiceImpl.java
package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.user.*;
import com.example.qtifood.entities.*;
import com.example.qtifood.enums.RoleType;
import com.example.qtifood.repositories.RoleRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private UserResponseDto toDto(User u) {
    Set<RoleType> roleNames = u.getRoles().stream()
        .map(Role::getName)
        .collect(Collectors.toSet());
    return new UserResponseDto(
        u.getId(), u.getFullName(), u.getEmail(), u.getPhone(),
        u.getAvatarUrl(), u.getDateOfBirth(), u.getGender(),
        u.getIsActive(), u.getCreatedAt(), u.getUpdatedAt(),
        roleNames
    );
    }

    @Override @Transactional(readOnly = true)
    public Page<UserResponseDto> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDto);
    }

    @Override @Transactional(readOnly = true)
    public UserResponseDto getUser(String id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        return toDto(u);
    }

    @Override
    public UserResponseDto createUser(CreateUserRequestDto dto) {
        if (dto.email() != null && userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Lấy danh sách roles từ request, nếu trống → mặc định CUSTOMER
        Set<RoleType> requested = (dto.roles() == null || dto.roles().isEmpty())
                ? Set.of(RoleType.CUSTOMER)
                : dto.roles();

        List<Role> roles = roleRepository.findByNameIn(requested);
        if (roles.size() != requested.size()) {
            // tìm role nào không tồn tại trong DB
            Set<RoleType> found = roles.stream().map(Role::getName).collect(Collectors.toSet());
            Set<RoleType> missing = new HashSet<>(requested); missing.removeAll(found);
            throw new IllegalArgumentException("Missing roles in DB: " + missing);
        }

        User u = User.builder()
                .id(dto.id())
                .fullName(dto.fullName())
                .email(dto.email())
                .phone(dto.phone())
                .password(passwordEncoder.encode(dto.password()))
                .avatarUrl(dto.avatarUrl())
                .dateOfBirth(dto.dateOfBirth())
                .gender(dto.gender())
                .isActive(dto.isActive() != null ? dto.isActive() : true)
                .roles(new HashSet<>(roles))
                .build();

        return toDto(userRepository.save(u));
    }

    @Override
    public UserResponseDto updateUser(String id, UpdateUserRequestDto dto) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        if (dto.fullName() != null) u.setFullName(dto.fullName());
        if (dto.email() != null)  u.setEmail(dto.email());
        if (dto.phone() != null)  u.setPhone(dto.phone());
        if (dto.avatarUrl() != null) u.setAvatarUrl(dto.avatarUrl());
        if (dto.dateOfBirth() != null) u.setDateOfBirth(dto.dateOfBirth());
        if (dto.gender() != null) u.setGender(dto.gender());
        if (dto.isActive() != null) u.setIsActive(dto.isActive());
        if (dto.password() != null && !dto.password().isBlank()) {
            u.setPassword(passwordEncoder.encode(dto.password()));
        }

        // Nếu có truyền roles → ghi đè danh sách roles
        if (dto.roles() != null) {
            if (dto.roles().isEmpty()) {
                u.getRoles().clear(); // cho phép xóa hết roles nếu muốn
            } else {
                List<Role> roles = roleRepository.findByNameIn(dto.roles());
                if (roles.size() != dto.roles().size()) {
                    Set<RoleType> found = roles.stream().map(Role::getName).collect(Collectors.toSet());
                    Set<RoleType> missing = new HashSet<>(dto.roles()); missing.removeAll(found);
                    throw new IllegalArgumentException("Missing roles in DB: " + missing);
                }
                u.setRoles(new HashSet<>(roles));
            }
        }

        return toDto(userRepository.save(u));
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) throw new IllegalArgumentException("User not found: " + id);
        userRepository.deleteById(id);
    }

    /* ========= tiện ích gán/bỏ role riêng lẻ ========= */
    public UserResponseDto addRole(String userId, RoleType roleType) {
    User u = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    Role r = roleRepository.findByName(roleType)
        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleType));
    u.getRoles().add(r);
    return toDto(userRepository.save(u));
    }

    public UserResponseDto removeRole(String userId, RoleType roleType) {
    User u = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    u.getRoles().removeIf(role -> role.getName() == roleType);
    return toDto(userRepository.save(u));
    }
}
