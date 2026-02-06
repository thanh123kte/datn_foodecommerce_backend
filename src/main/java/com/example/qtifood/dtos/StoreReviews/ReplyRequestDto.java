package com.example.qtifood.dtos.StoreReviews;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReplyRequestDto {
    @NotBlank(message = "Reply cannot be empty")
    private String reply;
}
