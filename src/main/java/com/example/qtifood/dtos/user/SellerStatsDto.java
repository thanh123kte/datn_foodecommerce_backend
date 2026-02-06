// src/main/java/com/example/qtifood/dtos/user/SellerStatsDto.java
package com.example.qtifood.dtos.user;

import java.time.LocalDateTime;
import java.util.Set;

import com.example.qtifood.dtos.Stores.StoreResponseDto;
import com.example.qtifood.enums.RoleType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SellerStatsDto {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<RoleType> roles;
    
    // Store information
    private StoreResponseDto store;
    
    // Statistics
    private Long totalProducts;
    private Long totalOrders;
    private Double totalRevenue;
}
