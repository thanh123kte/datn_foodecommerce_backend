package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.Messages.CreateMessageDto;
import com.example.qtifood.dtos.Messages.MessageResponseDto;
import com.example.qtifood.entities.Conversation;
import com.example.qtifood.entities.Message;
import com.example.qtifood.entities.User;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.mappers.MessageMapper;
import com.example.qtifood.repositories.ConversationRepository;
import com.example.qtifood.repositories.MessageRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    @Override
    public MessageResponseDto sendMessage(String senderId, CreateMessageDto dto) {
        // Validate sender exists
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found with id: " + senderId));

        // Validate conversation exists
        Conversation conversation = conversationRepository.findById(dto.getConversationId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + dto.getConversationId()));

        // Verify sender is part of the conversation
        if (!conversation.getCustomer().getId().equals(senderId) && 
            !conversation.getSeller().getId().equals(senderId)) {
            throw new ResourceNotFoundException("Sender is not part of this conversation");
        }

        // Create new message
        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(dto.getContent())
                .messageType(dto.getMessageType())
                .build();

        message = messageRepository.save(message);
        return MessageMapper.toDto(message);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageResponseDto> getMessages(Long conversationId, String userId, Pageable pageable) {
        // Validate conversation exists and user is part of it
        validateUserInConversation(conversationId, userId);

        Page<Message> messages = messageRepository.findByConversationIdWithSender(conversationId, pageable);
        return messages.map(MessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDto> getMessages(Long conversationId, String userId) {
        // Validate conversation exists and user is part of it
        validateUserInConversation(conversationId, userId);

        List<Message> messages = messageRepository.findByConversationIdWithSender(conversationId);
        return MessageMapper.toDtoList(messages);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageResponseDto getLastMessage(Long conversationId) {
        // Dùng PageRequest để lấy chỉ 1 message mới nhất
        Pageable pageable = PageRequest.of(0, 1);
        List<Message> messages = messageRepository.findLastMessageByConversationId(conversationId, pageable);
        
        if (!messages.isEmpty()) {
            return MessageMapper.toDto(messages.get(0));
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getMessagesCount(Long conversationId) {
        return messageRepository.countByConversationId(conversationId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadMessagesCount(Long conversationId, String userId) {
        // Validate conversation exists and user is part of it
        validateUserInConversation(conversationId, userId);

        return messageRepository.countUnreadMessages(conversationId, userId);
    }

    @Override
    public void deleteMessage(Long messageId, String senderId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        // Verify the message belongs to the sender
        if (!message.getSender().getId().equals(senderId)) {
            throw new ResourceNotFoundException("Message not found for this sender");
        }

        messageRepository.delete(message);
    }

    private void validateUserInConversation(Long conversationId, String userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + conversationId));

        if (!conversation.getCustomer().getId().equals(userId) && 
            !conversation.getSeller().getId().equals(userId)) {
            throw new ResourceNotFoundException("User is not part of this conversation");
        }
    }
}