package com.example.qtifood.services;

import com.example.qtifood.dtos.Conversations.CreateConversationDto;
import com.example.qtifood.dtos.Conversations.ConversationResponseDto;
import com.example.qtifood.dtos.Conversations.ConversationDetailDto;

import java.util.List;

public interface ConversationService {
    
    ConversationResponseDto createConversation(CreateConversationDto dto);
    
    ConversationResponseDto getOrCreateConversation(String customerId, String sellerId);
    
    List<ConversationResponseDto> getConversationsByCustomer(String customerId);
    
    List<ConversationResponseDto> getConversationsBySeller(String sellerId);
    
    List<ConversationResponseDto> getConversationsByUser(String userId);
    
    ConversationDetailDto getConversationDetail(Long conversationId, String userId);
    
    void deleteConversation(Long conversationId, String userId);
    
    Long getConversationsCount(String userId);
    
    boolean hasConversation(String customerId, String sellerId);
    
    /**
     * Mark all messages in a conversation as read for a specific user
     * @param conversationId ID of the conversation
     * @param userId ID of the user marking messages as read
     */
    void markConversationAsRead(Long conversationId, String userId);
}