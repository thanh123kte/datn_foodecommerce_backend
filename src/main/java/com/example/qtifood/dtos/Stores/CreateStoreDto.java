package com.example.qtifood.dtos.Stores;

import java.time.LocalTime;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateStoreDto {
    @NotNull
    private String ownerId;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String address;

    private String description;

    private Double latitude;   // gửi double; server sẽ convert BigDecimal
    private Double longitude;

    @Size(max = 20)
    private String phone;

    @Email
    @Size(max = 100)
    private String email;

    private String imageUrl;

    private LocalTime openTime;
    private LocalTime closeTime;
}
