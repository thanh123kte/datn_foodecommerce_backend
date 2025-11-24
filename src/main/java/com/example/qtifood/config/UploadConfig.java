package com.example.qtifood.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.upload")
@Getter
@Setter
public class UploadConfig {
    private String dir = "uploads";
    private long maxFileSize = 10485760; // 10MB
    private String[] allowedImageTypes = {"image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"};
}