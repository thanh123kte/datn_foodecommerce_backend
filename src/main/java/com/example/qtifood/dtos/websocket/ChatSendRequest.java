package com.example.qtifood.dtos.websocket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for sending a chat message via WebSocket
 * 
 * Client sends this to: /app/chat/conversations/{conversationId}/send
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSendRequest {
    
    /**
     * Message content (required, trimmed, 1-2000 characters)
     */
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 1, max = 2000, message = "Content must be between 1 and 2000 characters")
    private String content;
    
    /**
     * Trim content before validation
     */
    public void setContent(String content) {
        this.content = content != null ? content.trim() : null;
    }
}
