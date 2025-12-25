package com.example.qtifood.services;

import com.example.qtifood.dtos.Messages.CreateMessageDto;
import com.example.qtifood.dtos.Messages.MessageResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageService {
    
    MessageResponseDto sendMessage(String senderId, CreateMessageDto dto);
    
    Page<MessageResponseDto> getMessages(Long conversationId, String userId, Pageable pageable);
    
    List<MessageResponseDto> getMessages(Long conversationId, String userId);
    
    MessageResponseDto getLastMessage(Long conversationId);
    
    Long getMessagesCount(Long conversationId);
    
    Long getUnreadMessagesCount(Long conversationId, String userId);
    
    void deleteMessage(Long messageId, String senderId);
    
    List<MessageResponseDto> getMessagesByConversationId(Long conversationId);
}