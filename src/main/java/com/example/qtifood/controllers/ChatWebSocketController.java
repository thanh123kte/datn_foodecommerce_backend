package com.example.qtifood.controllers;

import com.example.qtifood.dtos.websocket.ChatMessageEvent;
import com.example.qtifood.dtos.websocket.ChatSendRequest;
import com.example.qtifood.entities.Conversation;
import com.example.qtifood.entities.Message;
import com.example.qtifood.entities.User;
import com.example.qtifood.enums.MessageType;
import com.example.qtifood.repositories.ConversationRepository;
import com.example.qtifood.repositories.MessageRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.services.FcmService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.format.DateTimeFormatter;

/**
 * WebSocket controller for real-time chat
 * 
 * Handles message sending via WebSocket/STOMP
 * - Validates conversation membership
 * - Persists messages to database
 * - Broadcasts to subscribers
 */
@Controller
public class ChatWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketController.class);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;
    
    public ChatWebSocketController(
            SimpMessagingTemplate messagingTemplate,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            UserRepository userRepository,
            FcmService fcmService) {
        this.messagingTemplate = messagingTemplate;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.fcmService = fcmService;
    }
    
    /**
     * Handle message send from client
     * 
     * Endpoint: /app/chat/conversations/{conversationId}/send
     * 
     * @param conversationId conversation ID from URL path
     * @param request message content
     * @param principal authenticated user (Principal.getName() = userId)
     */
    @MessageMapping("/chat/conversations/{conversationId}/send")
    @Transactional
    public void sendMessage(
            @DestinationVariable Long conversationId,
            @Valid @Payload ChatSendRequest request,
            Principal principal) {
        
        String senderId = principal != null ? principal.getName() : null;
        
        logger.info("WS message received: conversationId={}, senderId={}, contentLength={}", 
                    conversationId, senderId, request.getContent().length());
        
        try {
            // Validate sender is authenticated
            if (senderId == null || senderId.trim().isEmpty()) {
                logger.error("Unauthorized: No senderId in Principal");
                sendErrorToUser(senderId, "Unauthorized: Please authenticate");
                return;
            }
            
            // Validate content
            String content = request.getContent();
            if (content == null || content.trim().isEmpty()) {
                logger.error("Invalid message: Content is blank");
                sendErrorToUser(senderId, "Invalid message: Content cannot be blank");
                return;
            }
            
            if (content.length() > 2000) {
                logger.error("Invalid message: Content too long ({})", content.length());
                sendErrorToUser(senderId, "Invalid message: Content exceeds 2000 characters");
                return;
            }
            
            // Get conversation
            Conversation conversation = conversationRepository.findById(conversationId)
                    .orElse(null);
            
            if (conversation == null) {
                logger.error("Conversation not found: conversationId={}", conversationId);
                sendErrorToUser(senderId, "Conversation not found");
                return;
            }
            
            // Validate sender is participant (either customer or seller)
            String customerId = conversation.getCustomer().getId();
            String sellerId = conversation.getSeller().getId();
            
            if (!senderId.equals(customerId) && !senderId.equals(sellerId)) {
                logger.error("Unauthorized: User {} is not participant in conversation {}", 
                           senderId, conversationId);
                sendErrorToUser(senderId, "Unauthorized: You are not a participant in this conversation");
                return;
            }
            
            // Get sender user
            User sender = userRepository.findById(senderId)
                    .orElse(null);
            
            if (sender == null) {
                logger.error("Sender not found: senderId={}", senderId);
                sendErrorToUser(senderId, "Sender not found");
                return;
            }
            
            // Persist message
            Message message = Message.builder()
                    .conversation(conversation)
                    .sender(sender)
                    .content(content.trim())
                    .messageType(MessageType.TEXT)
                    .build();
            
            Message savedMessage = messageRepository.save(message);
            
            // Increment unread count for the receiver
            conversation.incrementUnreadCount(senderId);
            conversationRepository.save(conversation);
            
            logger.info("Message persisted: messageId={}, conversationId={}, senderId={}", 
                       savedMessage.getId(), conversationId, senderId);
            
            // Broadcast to all subscribers of this conversation
            ChatMessageEvent event = ChatMessageEvent.builder()
                    .id(savedMessage.getId())
                    .conversationId(conversationId)
                    .senderId(senderId)
                    .senderName(sender.getFullName())
                    .content(savedMessage.getContent())
                    .messageType("TEXT")
                    .createdAt(savedMessage.getCreatedAt().format(ISO_FORMATTER))
                    .isRead(false)
                    .build();
            
            messagingTemplate.convertAndSend(
                    "/topic/chat/conversations/" + conversationId,
                    event
            );
            
            logger.info("Message broadcasted: conversationId={}, messageId={}", 
                       conversationId, savedMessage.getId());

                // Send FCM notification to receiver
                String receiverId = senderId.equals(customerId) ? sellerId : customerId;
                String title = "Tin nhắn mới";
                String body = sender.getFullName() != null ?
                    (sender.getFullName() + ": " + truncateContent(savedMessage.getContent())) :
                    ("Bạn có tin nhắn mới: " + truncateContent(savedMessage.getContent()));
                fcmService.sendNotification(
                    receiverId,
                    title,
                    body,
                    "CHAT",
                    java.util.Map.of(
                        "conversationId", String.valueOf(conversationId),
                        "messageId", String.valueOf(savedMessage.getId()),
                        "senderId", senderId
                    )
                );
                logger.info("FCM sent to receiverId={} for conversationId={}", receiverId, conversationId);
            
        } catch (Exception e) {
            logger.error("Error processing message: conversationId={}, senderId={}, error={}", 
                        conversationId, senderId, e.getMessage(), e);
            sendErrorToUser(senderId, "Internal server error: " + e.getMessage());
        }
    }
    
    /**
     * Send error message to specific user
     * 
     * @param userId user ID to send error to
     * @param errorMessage error message
     */
    private void sendErrorToUser(String userId, String errorMessage) {
        if (userId == null) return;
        
        try {
            messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/errors",
                    new ErrorResponse(errorMessage)
            );
        } catch (Exception e) {
            logger.error("Failed to send error to user {}: {}", userId, e.getMessage());
        }
    }
    
    /**
     * Error response DTO
     */
    private record ErrorResponse(String message) {}

    private String truncateContent(String content) {
        if (content == null) return "";
        String trimmed = content.trim();
        return trimmed.length() > 60 ? trimmed.substring(0, 57) + "..." : trimmed;
    }
}
