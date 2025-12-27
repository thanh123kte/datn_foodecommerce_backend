package com.example.qtifood.dtos.Chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatbotMessageResponse {
    private Long conversationId;
    private String botUserId;
    private String reply;
    private List<ToolTraceEntry> toolTrace;
}
