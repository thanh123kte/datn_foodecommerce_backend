package com.example.qtifood.mappers;

import com.example.qtifood.dtos.Notifications.CreateNotificationDto;
import com.example.qtifood.dtos.Notifications.NotificationResponseDto;
import com.example.qtifood.entities.Notification;
import com.example.qtifood.entities.User;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toEntity(CreateNotificationDto dto, User user) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());
        notification.setType(dto.getType());
        notification.setEntityId(dto.getEntityId());
        notification.setEntityType(dto.getEntityType());
        notification.setIsRead(false);
        return notification;
    }

    public NotificationResponseDto toResponseDto(Notification notification) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUser().getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setIsRead(notification.getIsRead());
        dto.setEntityId(notification.getEntityId());
        dto.setEntityType(notification.getEntityType());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}