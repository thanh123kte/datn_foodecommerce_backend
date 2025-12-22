package com.example.qtifood.dtos.Stores;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyStoreDto {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double distanceKm;
    private String imageUrl;
}
