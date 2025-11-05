package com.example.qtifood.dtos.Notifications;

import com.example.qtifood.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateNotificationDto {
    
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;
    
    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    private String message;
    
    @NotNull(message = "Notification type is required")
    private NotificationType type;
    
    private Long entityId;
    
    @Size(max = 50, message = "Entity type must not exceed 50 characters")
    private String entityType;
}