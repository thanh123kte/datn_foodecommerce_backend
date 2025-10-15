package com.example.qtifood.mappers;

import java.math.BigDecimal;

import com.example.qtifood.dtos.Stores.StoreResponseDto;
import com.example.qtifood.entities.Store;

public class StoreMapper {
    public static StoreResponseDto toDto(Store s) {
        if (s == null) return null;
        return StoreResponseDto.builder()
            .id(s.getId())
            .ownerId(s.getOwner() != null ? s.getOwner().getId() : null)
            .name(s.getName())
            .description(s.getDescription())
            .address(s.getAddress())
            .latitude(s.getLatitude() == null ? null : s.getLatitude().doubleValue())
            .longitude(s.getLongitude() == null ? null : s.getLongitude().doubleValue())
            .phone(s.getPhone())
            .email(s.getEmail())
            .imageUrl(s.getImageUrl())
            .status(s.getStatus())
            .opStatus(s.getOpStatus())
            .openTime(s.getOpenTime())  
            .closeTime(s.getCloseTime())
            .createdAt(s.getCreatedAt())
            .updatedAt(s.getUpdatedAt())
            .build();
    }

    // helper
    public static BigDecimal toBD(Double v) {
        return v == null ? null : BigDecimal.valueOf(v);
    }
}
