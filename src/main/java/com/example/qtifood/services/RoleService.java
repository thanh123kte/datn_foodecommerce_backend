// src/main/java/com/example/qtifood/services/RoleService.java
package com.example.qtifood.services;

import com.example.qtifood.entities.Role;
import com.example.qtifood.enums.RoleType;

import java.util.List;

public interface RoleService {
    Role createRole(RoleType name, String description);
    List<Role> getAllRoles();
    Role getRoleByName(RoleType name);
}
