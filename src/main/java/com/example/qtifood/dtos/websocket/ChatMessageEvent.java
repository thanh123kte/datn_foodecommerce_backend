package com.example.qtifood.dtos.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event DTO for broadcasting chat messages via WebSocket
 * 
 * Server broadcasts this to: /topic/chat/conversations/{conversationId}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageEvent {
    
    /**
     * Message ID from database
     */
    private Long id;
    
    /**
     * Conversation ID this message belongs to
     */
    private Long conversationId;
    
    /**
     * Sender's user ID (Firebase UID)
     */
    private String senderId;
    
    /**
     * Sender's display name
     */
    private String senderName;
    
    /**
     * Message content
     */
    private String content;
    
    /**
     * Message type (currently only "TEXT")
     */
    private String messageType;
    
    /**
     * Creation timestamp (ISO-8601 format)
     */
    private String createdAt;
    
    /**
     * Whether the message has been read
     */
    private Boolean isRead;
}
