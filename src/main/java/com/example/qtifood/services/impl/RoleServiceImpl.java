// src/main/java/com/example/qtifood/services/impl/RoleServiceImpl.java
package com.example.qtifood.services.impl;

import com.example.qtifood.entities.Role;
import com.example.qtifood.enums.RoleType;
import com.example.qtifood.repositories.RoleRepository;
import com.example.qtifood.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role createRole(RoleType name, String description) {
        Role role = Role.builder()
                .name(name)
                .description(description)
                .build();
        return roleRepository.save(role);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleByName(RoleType name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + name));
    }
}
