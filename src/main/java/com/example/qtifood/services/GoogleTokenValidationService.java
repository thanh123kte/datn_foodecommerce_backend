package com.example.qtifood.services;

import com.example.qtifood.exceptions.BadRequestException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GoogleTokenValidationService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public AuthService.GoogleUserInfo validateToken(String googleToken) {
        try {
            String url = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + googleToken;
            String response = restTemplate.getForObject(url, String.class);
            
            JsonNode jsonNode = objectMapper.readTree(response);
            
            if (jsonNode.has("error")) {
                throw new BadRequestException("Invalid Google token");
            }
            
            String email = jsonNode.get("email").asText();
            String name = jsonNode.get("name").asText();
            String picture = jsonNode.has("picture") ? jsonNode.get("picture").asText() : null;
            
            return new AuthService.GoogleUserInfo(email, name, picture);
            
        } catch (Exception e) {
            throw new BadRequestException("Failed to validate Google token: " + e.getMessage());
        }
    }
}