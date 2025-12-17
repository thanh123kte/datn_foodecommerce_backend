package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.Messages.CreateMessageDto;
import com.example.qtifood.dtos.Messages.MessageResponseDto;
import com.example.qtifood.dtos.websocket.ChatMessageEvent;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

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
        
        // Increment unread count for the receiver
        conversation.incrementUnreadCount(senderId);
        conversationRepository.save(conversation);
        
        logger.info("Message saved via REST API: messageId={}, conversationId={}, senderId={}",
                   message.getId(), dto.getConversationId(), senderId);
        
        // Broadcast to WebSocket subscribers
        try {
            ChatMessageEvent event = ChatMessageEvent.builder()
                    .id(message.getId())
                    .conversationId(dto.getConversationId())
                    .senderId(senderId)
                    .senderName(sender.getFullName())
                    .content(message.getContent())
                    .messageType(dto.getMessageType().name())
                    .createdAt(message.getCreatedAt().format(ISO_FORMATTER))
                    .isRead(false)
                    .build();
            
            messagingTemplate.convertAndSend(
                    "/topic/chat/conversations/" + dto.getConversationId(),
                    event
            );
            
            logger.info("Message broadcasted via WebSocket: conversationId={}, messageId={}",
                       dto.getConversationId(), message.getId());
        } catch (Exception e) {
            logger.error("Failed to broadcast message via WebSocket: {}", e.getMessage(), e);
            // Continue even if broadcast fails - message is already saved
        }
        
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

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDto> getMessagesByConversationId(Long conversationId) {
        // Validate conversation exists
        if (!conversationRepository.existsById(conversationId)) {
            throw new ResourceNotFoundException("Conversation not found with id: " + conversationId);
        }

        // Get all messages sorted by creation time ascending
        List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        
        return MessageMapper.toDtoList(messages);
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