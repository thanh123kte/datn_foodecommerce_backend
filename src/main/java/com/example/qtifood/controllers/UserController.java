package com.example.qtifood.controllers;

import com.example.qtifood.dtos.user.CreateUserRequestDto;
import com.example.qtifood.dtos.user.UpdateUserRequestDto;
import com.example.qtifood.dtos.user.UserResponseDto;
import com.example.qtifood.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    
    private final UserService userService;

    // Get All users
    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    // Create user
    @PostMapping
    public UserResponseDto createUser(@RequestBody CreateUserRequestDto dto) {
        return userService.createUser(dto);
    }

    // Update user
    @PutMapping("/{id}")
    public UserResponseDto updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequestDto dto
    ) {
        return userService.updateUser(id, dto);
    }

    // Delete user
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "User " + id + " deleted successfully.";
        
    }
}
