package com.example.qtifood.services;

import com.example.qtifood.dtos.Conversations.CreateConversationDto;
import com.example.qtifood.dtos.Conversations.ConversationResponseDto;
import com.example.qtifood.dtos.Conversations.ConversationDetailDto;

import java.util.List;

public interface ConversationService {
    
    ConversationResponseDto createConversation(CreateConversationDto dto);
    
    ConversationResponseDto getOrCreateConversation(Long customerId, Long sellerId);
    
    List<ConversationResponseDto> getConversationsByCustomer(Long customerId);
    
    List<ConversationResponseDto> getConversationsBySeller(Long sellerId);
    
    List<ConversationResponseDto> getConversationsByUser(Long userId);
    
    ConversationDetailDto getConversationDetail(Long conversationId, Long userId);
    
    void deleteConversation(Long conversationId, Long userId);
    
    Long getConversationsCount(Long userId);
    
    boolean hasConversation(Long customerId, Long sellerId);
}