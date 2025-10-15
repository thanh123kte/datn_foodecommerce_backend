
package com.example.qtifood.repositories;

import com.example.qtifood.entities.Role;
import com.example.qtifood.enums.RoleType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
    List<Role> findByNameIn(Set<RoleType> names);
}
