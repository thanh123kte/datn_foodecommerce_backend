package com.example.qtifood.dtos.Stores;

import java.time.LocalTime;

import com.example.qtifood.entities.StoreStatus;
import lombok.Data;

@Data
public class UpdateStoreDto {
    private String name;
    private String address;
    private String description;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String email;
    private String imageUrl;
    private LocalTime openTime;
    private LocalTime closeTime;
    private StoreStatus status; // cho phép đổi trạng thái nếu cần
}
