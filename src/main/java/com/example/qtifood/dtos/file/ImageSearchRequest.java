package com.example.qtifood.dtos.file;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageSearchRequest {
    @NotBlank(message = "Base64 image string cannot be blank")
    @JsonProperty("base64_image_string")
    private String base64ImageString;
}
