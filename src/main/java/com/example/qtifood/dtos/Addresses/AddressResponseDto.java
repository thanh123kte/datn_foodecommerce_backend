package com.example.qtifood.dtos.Addresses;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponseDto {
    private Long id;
    private String receiver;
    private String phone;
    private String address;
    private Double latitude;
    private Double longitude;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
