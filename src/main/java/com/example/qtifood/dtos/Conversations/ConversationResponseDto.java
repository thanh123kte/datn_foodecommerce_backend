package com.example.qtifood.dtos.Conversations;

import com.example.qtifood.dtos.Messages.MessageResponseDto;
import com.example.qtifood.dtos.user.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationResponseDto {
    
    private Long id;
    private UserResponseDto customer;
    private UserResponseDto seller;
    private MessageResponseDto lastMessage;
    private Integer unreadCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
}