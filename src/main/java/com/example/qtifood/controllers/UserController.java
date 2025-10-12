// src/main/java/com/example/qtifood/controllers/UserController.java
package com.example.qtifood.controllers;

import com.example.qtifood.dtos.user.*;
import com.example.qtifood.entities.RoleType;
import com.example.qtifood.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public UserResponseDto get(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid CreateUserRequestDto dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PutMapping("/{id}")
    public UserResponseDto update(@PathVariable Long id, @RequestBody @Valid UpdateUserRequestDto dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /* ====== Add/Remove role ====== */
    @PostMapping("/{id}/roles/{role}")
    public UserResponseDto addRole(@PathVariable Long id, @PathVariable RoleType role) {
        return ((com.example.qtifood.services.impl.UserServiceImpl) userService).addRole(id, role);
    }

    @DeleteMapping("/{id}/roles/{role}")
    public UserResponseDto removeRole(@PathVariable Long id, @PathVariable RoleType role) {
        return ((com.example.qtifood.services.impl.UserServiceImpl) userService).removeRole(id, role);
    }
}
