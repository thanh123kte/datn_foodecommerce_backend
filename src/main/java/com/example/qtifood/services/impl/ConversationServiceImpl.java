package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.Conversations.CreateConversationDto;
import com.example.qtifood.dtos.Conversations.ConversationResponseDto;
import com.example.qtifood.dtos.Conversations.ConversationDetailDto;
import com.example.qtifood.entities.Conversation;
import com.example.qtifood.entities.Message;
import com.example.qtifood.entities.User;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.exceptions.EntityDuplicateException;
import com.example.qtifood.mappers.ConversationMapper;
import com.example.qtifood.repositories.ConversationRepository;
import com.example.qtifood.repositories.MessageRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.services.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public ConversationResponseDto createConversation(CreateConversationDto dto) {
        // Validate customer exists
        User customer = userRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + dto.getCustomerId()));

        // Validate seller exists
        User seller = userRepository.findById(dto.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + dto.getSellerId()));

        // Check if conversation already exists
        if (conversationRepository.existsByCustomerIdAndSellerId(dto.getCustomerId(), dto.getSellerId())) {
            throw new EntityDuplicateException("Conversation already exists between customer and seller");
        }

        // Create new conversation
        Conversation conversation = Conversation.builder()
                .customer(customer)
                .seller(seller)
                .build();

        conversation = conversationRepository.save(conversation);
        return ConversationMapper.toDto(conversation);
    }

    @Override
    public ConversationResponseDto getOrCreateConversation(String customerId, String sellerId) {
        // Check if conversation already exists
        Optional<Conversation> existingConversation = conversationRepository
                .findByCustomerIdAndSellerId(customerId, sellerId);

        if (existingConversation.isPresent()) {
            return ConversationMapper.toDto(existingConversation.get());
        }

        // Create new conversation
        CreateConversationDto dto = CreateConversationDto.builder()
                .customerId(customerId)
                .sellerId(sellerId)
                .build();

        return createConversation(dto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponseDto> getConversationsByCustomer(String customerId) {
        // Validate customer exists
        if (!userRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }

        List<Conversation> conversations = conversationRepository.findByCustomerIdWithDetails(customerId);
        return conversations.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponseDto> getConversationsBySeller(String sellerId) {
        // Validate seller exists
        if (!userRepository.existsById(sellerId)) {
            throw new ResourceNotFoundException("Seller not found with id: " + sellerId);
        }

        List<Conversation> conversations = conversationRepository.findBySellerIdWithDetails(sellerId);
        return conversations.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponseDto> getConversationsByUser(String userId) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<Conversation> conversations = conversationRepository.findByUserIdWithDetails(userId);
        return conversations.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationDetailDto getConversationDetail(Long conversationId, String userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + conversationId));

        // Verify user is part of the conversation
        if (!conversation.getCustomer().getId().equals(userId) && 
            !conversation.getSeller().getId().equals(userId)) {
            throw new ResourceNotFoundException("User is not part of this conversation");
        }

        // Load messages separately
        List<Message> messages = messageRepository.findByConversationIdWithSender(conversationId);

        return ConversationMapper.toDetailDto(conversation, messages);
    }

    @Override
    public void deleteConversation(Long conversationId, String userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with id: " + conversationId));

        // Verify user is part of the conversation
        if (!conversation.getCustomer().getId().equals(userId) && 
            !conversation.getSeller().getId().equals(userId)) {
            throw new ResourceNotFoundException("User is not part of this conversation");
        }

        conversationRepository.delete(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getConversationsCount(String userId) {
        // Validate user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        Long customerCount = conversationRepository.countByCustomerId(userId);
        Long sellerCount = conversationRepository.countBySellerId(userId);
        return customerCount + sellerCount;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasConversation(String customerId, String sellerId) {
        return conversationRepository.existsByCustomerIdAndSellerId(customerId, sellerId);
    }

    private ConversationResponseDto mapToResponseDto(Conversation conversation) {
        // Get last message
        List<Message> lastMessages = messageRepository
                .findLastMessageByConversationId(conversation.getId(), PageRequest.of(0, 1));
        Message lastMessage = lastMessages.isEmpty() ? null : lastMessages.get(0);

        // Get unread count (simplified - assuming all messages from other party are unread)
        Long unreadCount = messageRepository.countUnreadMessages(
                conversation.getId(), conversation.getCustomer().getId());

        return ConversationMapper.toDto(conversation, lastMessage, unreadCount.intValue());
    }
}