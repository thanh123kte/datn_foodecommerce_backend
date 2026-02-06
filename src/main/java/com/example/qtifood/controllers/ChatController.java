package com.example.qtifood.controllers;

import com.example.qtifood.dtos.Messages.CreateMessageDto;
import com.example.qtifood.dtos.Messages.MessageResponseDto;
import com.example.qtifood.services.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Validated
public class ChatController {

    private final MessageService messageService;

    /**
     * Get all messages in a conversation, sorted by creation time
     * @param conversationId The conversation ID
     * @return List of messages sorted by createdAt ascending
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageResponseDto>> getConversationMessages(
            @PathVariable Long conversationId) {
        List<MessageResponseDto> messages = messageService.getMessagesByConversationId(conversationId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Send a message via REST API (fallback when WebSocket is not available)
     * @param senderId The sender's user ID
     * @param dto Message data
     * @return The created message
     */
    @PostMapping("/messages")
    public ResponseEntity<MessageResponseDto> sendMessage(
            @RequestParam String senderId,
            @Valid @RequestBody CreateMessageDto dto) {
        MessageResponseDto message = messageService.sendMessage(senderId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
}
