package com.example.qtifood.dtos.Addresses;

import lombok.Data;

@Data
public class UpdateAddressDto {
    private String receiver;
    private String phone;
    private String address;
    private Double latitude;
    private Double longitude;
    private Boolean isDefault;
}
