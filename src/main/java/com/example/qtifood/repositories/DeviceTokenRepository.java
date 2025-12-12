package com.example.qtifood.repositories;

import com.example.qtifood.entities.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    
    List<DeviceToken> findByUserId(String userId);
    
    List<DeviceToken> findByUserIdAndRole(String userId, String role);
    
    Optional<DeviceToken> findByUserIdAndToken(String userId, String token);
    
    @Query("SELECT d FROM DeviceToken d WHERE d.userId IN :userIds AND d.role = :role")
    List<DeviceToken> findByUserIdsAndRole(@Param("userIds") List<String> userIds, @Param("role") String role);
    
    void deleteByUserId(String userId);
    
    void deleteByUserIdAndToken(String userId, String token);
    
    long countByUserId(String userId);
}
