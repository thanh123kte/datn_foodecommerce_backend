package com.example.qtifood.controllers;

import com.example.qtifood.dtos.Chatbot.ChatbotMessageRequest;
import com.example.qtifood.dtos.Chatbot.ChatbotMessageResponse;
import com.example.qtifood.services.chatbot.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatbotMessageResponse> sendMessage(@Valid @RequestBody ChatbotMessageRequest request) {
        ChatbotMessageResponse response = chatbotService.handleMessage(request);
        return ResponseEntity.ok(response);
    }
}
