package com.example.qtifood.mappers;

import com.example.qtifood.dtos.Messages.MessageResponseDto;
import com.example.qtifood.entities.Message;

import java.util.List;
import java.util.stream.Collectors;

public class MessageMapper {

    public static MessageResponseDto toDto(Message message) {
        if (message == null) {
            return null;
        }

        return MessageResponseDto.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .sender(UserMapper.toDto(message.getSender()))
                .content(message.getContent())
                .messageType(message.getMessageType())
                .createdAt(message.getCreatedAt())
                .build();
    }

    public static List<MessageResponseDto> toDtoList(List<Message> messages) {
        if (messages == null) {
            return null;
        }
        return messages.stream()
                .map(MessageMapper::toDto)
                .collect(Collectors.toList());
    }
}