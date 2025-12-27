package com.example.qtifood.dtos.Chatbot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotMessageRequest {
    @NotBlank(message = "customerId is required")
    private String customerId;

    @NotBlank(message = "text is required")
    private String text;

    @Builder.Default
    @NotNull
    private Long conversationId = 0L;
}
