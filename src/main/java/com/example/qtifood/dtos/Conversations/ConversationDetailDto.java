package com.example.qtifood.dtos.Conversations;

import com.example.qtifood.dtos.Messages.MessageResponseDto;
import com.example.qtifood.dtos.user.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDetailDto {
    
    private Long id;
    private UserResponseDto customer;
    private UserResponseDto seller;
    private List<MessageResponseDto> messages;
    private LocalDateTime createdAt;
}