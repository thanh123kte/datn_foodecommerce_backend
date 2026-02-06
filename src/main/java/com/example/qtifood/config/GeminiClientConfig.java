package com.example.qtifood.config;

import com.google.genai.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GeminiClientConfig {

    private final GeminiSettings geminiSettings;

    @Bean(destroyMethod = "close")
    public Client geminiClient() {
        return Client.builder()
                .apiKey(geminiSettings.apiKey())
                .build();
    }

    @Bean("geminiModelName")
    public String geminiModelName() {
        return geminiSettings.modelName();
    }
}
