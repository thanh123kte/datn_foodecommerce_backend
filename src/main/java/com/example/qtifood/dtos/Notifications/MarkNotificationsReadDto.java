package com.example.qtifood.dtos.Notifications;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class MarkNotificationsReadDto {
    
    @NotEmpty(message = "Notification IDs cannot be empty")
    private List<Long> notificationIds;
}