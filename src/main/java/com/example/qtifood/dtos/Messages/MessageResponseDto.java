package com.example.qtifood.dtos.Messages;

import com.example.qtifood.dtos.user.UserResponseDto;
import com.example.qtifood.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponseDto {
    
    private Long id;
    private Long conversationId;
    private UserResponseDto sender;
    private String content;
    private MessageType messageType;
    private LocalDateTime createdAt;
}