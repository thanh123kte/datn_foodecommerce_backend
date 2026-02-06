package com.example.qtifood.config;

import com.example.qtifood.constants.ChatbotConstants;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class DotenvConfig {

    @Bean
    public GeminiSettings geminiSettings() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        String apiKey = firstNonBlank(
                System.getenv("GEMINI_API_KEY"),
                System.getProperty("GEMINI_API_KEY"),
                dotenv.get("GEMINI_API_KEY")
        );
        String modelName = firstNonBlank(
                System.getenv("GEMINI_MODEL"),
                System.getProperty("GEMINI_MODEL"),
                dotenv.get("GEMINI_MODEL"),
                ChatbotConstants.DEFAULT_MODEL_NAME
        );

        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("Missing GEMINI_API_KEY for Gemini client configuration");
        }

        return new GeminiSettings(apiKey.trim(), modelName);
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }
}
