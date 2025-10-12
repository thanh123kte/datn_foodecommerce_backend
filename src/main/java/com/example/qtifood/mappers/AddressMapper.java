package com.example.qtifood.mappers;

import com.example.qtifood.dtos.Addresses.AddressResponseDto;
import com.example.qtifood.entities.Address;

public class AddressMapper {
    public static AddressResponseDto toDto(Address a) {
        if (a == null) return null;
        return AddressResponseDto.builder()
                .id(a.getId())
                .receiver(a.getReceiver())
                .phone(a.getPhone())
                .address(a.getAddress())
                // Nếu entity dùng BigDecimal:
                .latitude(a.getLatitude() == null ? null : a.getLatitude().doubleValue())
                .longitude(a.getLongitude() == null ? null : a.getLongitude().doubleValue())
                .isDefault(a.getIsDefault())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
