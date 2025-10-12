package com.example.qtifood.services;

import com.example.qtifood.dtos.user.CreateUserRequestDto;
import com.example.qtifood.dtos.user.UpdateUserRequestDto;
import com.example.qtifood.dtos.user.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getAllUsers();
    UserResponseDto createUser(CreateUserRequestDto dto);
    UserResponseDto updateUser(Long id, UpdateUserRequestDto dto);
    void deleteUser(Long id);
}
