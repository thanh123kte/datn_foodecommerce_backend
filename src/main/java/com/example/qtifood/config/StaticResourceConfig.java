package com.example.qtifood.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@org.springframework.lang.NonNull ResourceHandlerRegistry registry) {
        // Lấy đường dẫn tuyệt đối đến thư mục uploads
        String uploadPath = Paths.get("").toAbsolutePath().toString() + "/uploads/";
        
        // Cấu hình để serve files từ thư mục uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath)
                .addResourceLocations("file:uploads/")
                .addResourceLocations("file:./uploads/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new org.springframework.web.servlet.resource.PathResourceResolver());

        // Cấu hình cho products folder
        registry.addResourceHandler("/products/**")
                .addResourceLocations("file:" + uploadPath + "products/")
                .addResourceLocations("file:uploads/products/")
                .addResourceLocations("file:./uploads/products/")
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new org.springframework.web.servlet.resource.PathResourceResolver());
    }
}