// src/main/java/com/example/qtifood/repositories/UserRepository.java
package com.example.qtifood.repositories;

import com.example.qtifood.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ADMIN'")
    List<User> findAdminUsers();
}
