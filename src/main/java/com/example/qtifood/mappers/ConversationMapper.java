package com.example.qtifood.mappers;

import com.example.qtifood.dtos.Conversations.ConversationResponseDto;
import com.example.qtifood.dtos.Conversations.ConversationDetailDto;
import com.example.qtifood.entities.Conversation;
import com.example.qtifood.entities.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ConversationMapper {

    public static ConversationResponseDto toDto(Conversation conversation) {
        return toDto(conversation, null, 0);
    }

    public static ConversationResponseDto toDto(Conversation conversation, Message lastMessage, Integer unreadCount) {
        if (conversation == null) {
            return null;
        }

        LocalDateTime lastMessageAt = lastMessage != null ? 
            lastMessage.getCreatedAt() : conversation.getCreatedAt();

        return ConversationResponseDto.builder()
                .id(conversation.getId())
                .customer(UserMapper.toDto(conversation.getCustomer()))
                .seller(UserMapper.toDto(conversation.getSeller()))
                .lastMessage(lastMessage != null ? MessageMapper.toDto(lastMessage) : null)
                .unreadCount(unreadCount != null ? unreadCount : 0)
                .createdAt(conversation.getCreatedAt())
                .lastMessageAt(lastMessageAt)
                .build();
    }

    public static ConversationDetailDto toDetailDto(Conversation conversation, List<Message> messages) {
        if (conversation == null) {
            return null;
        }

        return ConversationDetailDto.builder()
                .id(conversation.getId())
                .customer(UserMapper.toDto(conversation.getCustomer()))
                .seller(UserMapper.toDto(conversation.getSeller()))
                .messages(messages != null ? MessageMapper.toDtoList(messages) : List.of())
                .createdAt(conversation.getCreatedAt())
                .build();
    }

    public static List<ConversationResponseDto> toDtoList(List<Conversation> conversations) {
        if (conversations == null) {
            return null;
        }
        return conversations.stream()
                .map(ConversationMapper::toDto)
                .collect(Collectors.toList());
    }
}