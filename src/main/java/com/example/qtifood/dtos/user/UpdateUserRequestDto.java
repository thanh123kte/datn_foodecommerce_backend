package com.example.qtifood.dtos.user;

import lombok.Data;

@Data
public class UpdateUserRequestDto {
    private String email;
    private String password;
    private String role;
    private Boolean active;
}
