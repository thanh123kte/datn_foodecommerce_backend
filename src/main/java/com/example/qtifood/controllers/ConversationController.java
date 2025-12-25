package com.example.qtifood.controllers;

import com.example.qtifood.dtos.Conversations.ConversationResponseDto;
import com.example.qtifood.dtos.Conversations.ConversationDetailDto;
import com.example.qtifood.services.ConversationService;
import lombok.RequiredArgsConstructor;
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

    // Lấy tất cả cuộc trò chuyện của một user (không phân biệt vai trò)
    @GetMapping
    public ResponseEntity<List<ConversationResponseDto>> getAllConversationsForUser(
            @RequestParam String userId) {
        List<ConversationResponseDto> conversations = conversationService.getConversationsByUser(userId);
        return ResponseEntity.ok(conversations);
    }

    @PostMapping("/get-or-create")
    public ResponseEntity<ConversationResponseDto> getOrCreateConversation(
            @RequestParam String customerId,
            @RequestParam String sellerId) {
        ConversationResponseDto conversation = conversationService.getOrCreateConversation(customerId, sellerId);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ConversationResponseDto>> getConversationsByUser(
            @PathVariable String userId) {
        List<ConversationResponseDto> conversations = conversationService.getConversationsByUser(userId);
        return ResponseEntity.ok(conversations);
    }
    
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<ConversationResponseDto>> getConversationsBySeller(
            @PathVariable String sellerId) {
        List<ConversationResponseDto> conversations = conversationService.getConversationsBySeller(sellerId);
        return ResponseEntity.ok(conversations);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ConversationResponseDto>> getConversationsByCustomer(
            @PathVariable String customerId) {
        List<ConversationResponseDto> conversations = conversationService.getConversationsByCustomer(customerId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/{conversationId}/user/{userId}")
    public ResponseEntity<ConversationDetailDto> getConversationDetail(
            @PathVariable Long conversationId,
            @PathVariable String userId) {
        ConversationDetailDto conversation = conversationService.getConversationDetail(conversationId, userId);
        return ResponseEntity.ok(conversation);
    }

    @DeleteMapping("/{conversationId}/user/{userId}")
    public ResponseEntity<Map<String, String>> deleteConversation(
            @PathVariable Long conversationId,
            @PathVariable String userId) {
        conversationService.deleteConversation(conversationId, userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Conversation deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{conversationId}/mark-read")
    public ResponseEntity<Map<String, String>> markConversationAsRead(
            @PathVariable Long conversationId,
            @RequestParam String userId) {
        conversationService.markConversationAsRead(conversationId, userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Conversation marked as read");
        return ResponseEntity.ok(response);
    }
}