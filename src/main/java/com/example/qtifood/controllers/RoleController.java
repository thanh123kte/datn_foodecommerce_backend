// src/main/java/com/example/qtifood/controllers/RoleController.java
package com.example.qtifood.controllers;

import com.example.qtifood.entities.Role;
import com.example.qtifood.entities.RoleType;
import com.example.qtifood.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PostMapping
    public Role createRole(@RequestParam RoleType name, @RequestParam(required = false) String description) {
        return roleService.createRole(name, description);
    }
}