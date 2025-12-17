package com.example.qtifood.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * WebSocket configuration for real-time chat
 * 
 * Endpoint: /ws
 * Broker: /topic (for broadcasting messages)
 * App prefix: /app (for client-to-server messages)
 * 
 * Example:
 * - Client sends to: /app/chat/conversations/{conversationId}/send
 * - Clients subscribe to: /topic/chat/conversations/{conversationId}
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
    
    private final WebSocketAuthInterceptor authInterceptor;
    
    public WebSocketConfig(WebSocketAuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable simple in-memory message broker for broadcasting
        // Messages to /topic will be routed to all subscribers
        registry.enableSimpleBroker("/topic");
        
        // Set application destination prefix
        // Messages from client to /app/* will be routed to @MessageMapping methods
        registry.setApplicationDestinationPrefixes("/app");
        
        logger.info("Message broker configured: /topic for broadcast, /app for client messages");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register WebSocket endpoint at /ws
        registry.addEndpoint("/ws")
                // Allow all origins for development
                // TODO: In production, restrict to specific domains
                .setAllowedOriginPatterns("*")
                // Enable SockJS fallback for browsers that don't support WebSocket
                .withSockJS();
        
        logger.info("STOMP endpoint registered at /ws with SockJS fallback");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        // Configure WebSocket transport settings
        registration
                // Max message size: 128KB
                .setMessageSizeLimit(128 * 1024)
                // Max buffer size: 512KB
                .setSendBufferSizeLimit(512 * 1024)
                // Heartbeat: send every 20s, expect every 20s
                .setSendTimeLimit(20 * 1000)
                .setTimeToFirstMessage(30 * 1000);
        
        logger.info("WebSocket transport configured with message size limits and heartbeat");
    }
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Register authentication interceptor
        registration.interceptors(authInterceptor);
        logger.info("WebSocket authentication interceptor registered");
    }
}
