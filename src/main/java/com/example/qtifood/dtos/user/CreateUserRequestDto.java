package com.example.qtifood.dtos.user;

import lombok.Data;

@Data
public class CreateUserRequestDto {
    private String username;
    private String email;
    private String password;
    private String role;
}
