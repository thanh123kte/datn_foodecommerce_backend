package com.example.qtifood.dtos.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private Boolean active;
}
