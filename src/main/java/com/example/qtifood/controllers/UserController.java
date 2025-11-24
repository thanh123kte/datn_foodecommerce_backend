// src/main/java/com/example/qtifood/controllers/UserController.java
package com.example.qtifood.controllers;

import com.example.qtifood.dtos.user.*;
import com.example.qtifood.enums.RoleType;
import com.example.qtifood.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Page<UserResponseDto> list(Pageable pageable) {
        return userService.getUsers(pageable);
    }

    @GetMapping("/{id}")
    public UserResponseDto get(@PathVariable String id) {
        return userService.getUser(id);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid CreateUserRequestDto dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PutMapping("/{id}")
    public UserResponseDto update(@PathVariable String id, @RequestBody @Valid UpdateUserRequestDto dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /* ====== Add/Remove role ====== */
    @PutMapping("/{id}/roles/{role}")
    public UserResponseDto addRole(@PathVariable String id, @PathVariable RoleType role) {
        return ((com.example.qtifood.services.impl.UserServiceImpl) userService).addRole(id, role);
    }

    @DeleteMapping("/{id}/roles/{role}")
    public UserResponseDto removeRole(@PathVariable String id, @PathVariable RoleType role) {
        return ((com.example.qtifood.services.impl.UserServiceImpl) userService).removeRole(id, role);
    }

    /* ====== Upload Avatar ====== */
    @PostMapping(value = "/{id}/avatar", consumes = "multipart/form-data")
    public ResponseEntity<UserResponseDto> uploadAvatar(
            @PathVariable String id,
            @RequestParam("avatar") MultipartFile avatarFile) {
        
        if (avatarFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        UserResponseDto updatedUser = userService.uploadAvatar(id, avatarFile);
        return ResponseEntity.ok(updatedUser);
    }

    /* ====== Delete Avatar ====== */
    @DeleteMapping("/{id}/avatar")
    public ResponseEntity<UserResponseDto> deleteAvatar(@PathVariable String id) {
        UserResponseDto updatedUser = userService.deleteAvatar(id);
        return ResponseEntity.ok(updatedUser);
    }
}
