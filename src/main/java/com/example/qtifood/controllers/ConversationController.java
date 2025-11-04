package com.example.qtifood.controllers;

import com.example.qtifood.dtos.Conversations.CreateConversationDto;
import com.example.qtifood.dtos.Conversations.ConversationResponseDto;
import com.example.qtifood.dtos.Conversations.ConversationDetailDto;
import com.example.qtifood.services.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Validated
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping("/get-or-create")
    public ResponseEntity<ConversationResponseDto> getOrCreateConversation(
            @RequestParam Long customerId,
            @RequestParam Long sellerId) {
        ConversationResponseDto conversation = conversationService.getOrCreateConversation(customerId, sellerId);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ConversationResponseDto>> getConversationsByUser(
            @PathVariable Long userId) {
        List<ConversationResponseDto> conversations = conversationService.getConversationsByUser(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/{conversationId}/user/{userId}")
    public ResponseEntity<ConversationDetailDto> getConversationDetail(
            @PathVariable Long conversationId,
            @PathVariable Long userId) {
        ConversationDetailDto conversation = conversationService.getConversationDetail(conversationId, userId);
        return ResponseEntity.ok(conversation);
    }

    @DeleteMapping("/{conversationId}/user/{userId}")
    public ResponseEntity<Map<String, String>> deleteConversation(
            @PathVariable Long conversationId,
            @PathVariable Long userId) {
        conversationService.deleteConversation(conversationId, userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Conversation deleted successfully");
        return ResponseEntity.ok(response);
    }
}