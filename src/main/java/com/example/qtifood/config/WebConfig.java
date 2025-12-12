package com.example.qtifood.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Commented out to avoid CORS conflict with SecurityConfiguration
// @Configuration
public class WebConfig implements WebMvcConfigurer {

    // CORS is now handled in SecurityConfiguration
    /*
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Cho phép tất cả origins (nên hạn chế trong production)
                .allowedOrigins("*")
                // Các HTTP methods được phép
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                // Headers được phép gửi từ client
                .allowedHeaders("*")
                // Headers được trả về trong response
                .exposedHeaders("Authorization", "Content-Type")
                // Cho phép credentials (cookies)
                .allowCredentials(false)
                // Thời gian cache preflight request (seconds)
                .maxAge(3600);
    }
    */
}
