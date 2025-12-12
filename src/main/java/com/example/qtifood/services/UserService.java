// src/main/java/com/example/qtifood/services/UserService.java
package com.example.qtifood.services;

import com.example.qtifood.dtos.user.CreateUserRequestDto;
import com.example.qtifood.dtos.user.UpdateUserRequestDto;
import com.example.qtifood.dtos.user.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponseDto> getUsers(Pageable pageable);
    UserResponseDto getUser(String id);
    UserResponseDto createUser(CreateUserRequestDto dto);
    UserResponseDto updateUser(String id, UpdateUserRequestDto dto);
    void deleteUser(String id);
}
