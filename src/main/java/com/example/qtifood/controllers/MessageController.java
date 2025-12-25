package com.example.qtifood.controllers;

import com.example.qtifood.dtos.Messages.CreateMessageDto;
import com.example.qtifood.dtos.Messages.MessageResponseDto;
import com.example.qtifood.services.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Validated
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/user/{senderId}")
    public ResponseEntity<MessageResponseDto> sendMessage(
            @PathVariable String senderId,
            @Valid @RequestBody CreateMessageDto dto) {
        MessageResponseDto message = messageService.sendMessage(senderId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping("/conversation/{conversationId}/user/{userId}")
    public ResponseEntity<Page<MessageResponseDto>> getMessages(
            @PathVariable Long conversationId,
            @PathVariable String userId,
            @PageableDefault(size = 50, sort = "createdAt") Pageable pageable) {
        Page<MessageResponseDto> messages = messageService.getMessages(conversationId, userId, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/conversation/{conversationId}/last")
    public ResponseEntity<MessageResponseDto> getLastMessage(@PathVariable Long conversationId) {
        MessageResponseDto message = messageService.getLastMessage(conversationId);
        if (message != null) {
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/conversation/{conversationId}/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadMessagesCount(
            @PathVariable Long conversationId,
            @PathVariable String userId) {
        Long count = messageService.getUnreadMessagesCount(conversationId, userId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{messageId}/user/{senderId}")
    public ResponseEntity<Map<String, String>> deleteMessage(
            @PathVariable Long messageId,
            @PathVariable String senderId) {
        messageService.deleteMessage(messageId, senderId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Message deleted successfully");
        return ResponseEntity.ok(response);
    }
}