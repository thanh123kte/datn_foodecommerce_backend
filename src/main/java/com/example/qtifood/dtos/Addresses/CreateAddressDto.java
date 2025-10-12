package com.example.qtifood.dtos.Addresses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAddressDto {
    @NotBlank
    private String receiver;

    @NotBlank
    private String phone;

    @NotBlank
    private String address;

    @NotNull
    private Long userId;

    private Double latitude;
    private Double longitude;
}
