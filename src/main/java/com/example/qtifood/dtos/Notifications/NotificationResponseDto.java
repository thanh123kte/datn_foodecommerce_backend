package com.example.qtifood.dtos.Notifications;

import com.example.qtifood.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDto {
    
    private Long id;
    private String userId;
    private String title;
    private String message;
    private NotificationType type;
    private Boolean isRead;
    private Long entityId;
    private String entityType;
    private LocalDateTime createdAt;
}