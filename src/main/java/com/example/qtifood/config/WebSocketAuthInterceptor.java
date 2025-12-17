package com.example.qtifood.config;

import com.example.qtifood.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Interceptor for WebSocket authentication
 * 
 * Validates JWT token on CONNECT and sets Principal with userId
 * 
 * Supports:
 * 1. Authorization: Bearer <token>
 * 2. X-User-ID: <userId> (dev fallback, TODO: remove in production)
 */
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);
    
    private final JwtUtils jwtUtils;
    
    public WebSocketAuthInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String userId = authenticateUser(accessor);
            
            if (userId != null) {
                // Set Principal with userId
                Authentication auth = new UsernamePasswordAuthenticationToken(
                    userId, null, new ArrayList<>()
                );
                accessor.setUser(auth);
                
                logger.info("WebSocket CONNECT authenticated: userId={}", userId);
            } else {
                logger.warn("WebSocket CONNECT failed: No valid authentication");
                // Continue without authentication - will be checked in message handler
            }
        }
        
        return message;
    }
    
    /**
     * Authenticate user from WebSocket headers
     * 
     * @param accessor STOMP header accessor
     * @return userId if authenticated, null otherwise
     */
    private String authenticateUser(StompHeaderAccessor accessor) {
        // Strategy 1: Authorization: Bearer <token>
        List<String> authHeaders = accessor.getNativeHeader("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    if (jwtUtils.validateToken(token)) {
                        String userId = jwtUtils.getUserIdFromToken(token);
                        logger.debug("Authenticated via JWT: userId={}", userId);
                        return userId;
                    }
                } catch (Exception e) {
                    logger.error("JWT validation failed: {}", e.getMessage());
                }
            }
        }
        
        // Strategy 2: X-User-ID header (dev fallback)
        // TODO: Remove this in production or restrict to development profile
        List<String> userIdHeaders = accessor.getNativeHeader("X-User-ID");
        if (userIdHeaders != null && !userIdHeaders.isEmpty()) {
            String userId = userIdHeaders.get(0);
            if (userId != null && !userId.trim().isEmpty()) {
                logger.warn("Authenticated via X-User-ID (DEV ONLY): userId={}", userId);
                return userId;
            }
        }
        
        return null;
    }
}
