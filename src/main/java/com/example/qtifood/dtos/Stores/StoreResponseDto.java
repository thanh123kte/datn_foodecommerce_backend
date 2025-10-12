package com.example.qtifood.dtos.Stores;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.qtifood.entities.StoreStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreResponseDto {
    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String email;
    private String imageUrl;
    private StoreStatus status;
    private LocalTime openTime;
    private LocalTime closeTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
