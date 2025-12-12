package com.example.qtifood.controllers;

import com.example.qtifood.dtos.Notifications.CreateNotificationDto;
import com.example.qtifood.dtos.Notifications.MarkNotificationsReadDto;
import com.example.qtifood.dtos.Notifications.NotificationResponseDto;
import com.example.qtifood.enums.NotificationType;
import com.example.qtifood.services.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    // Tạo notification mới
    @PostMapping("/user/{userId}")
    public ResponseEntity<NotificationResponseDto> createNotification(
            @PathVariable String userId,
            @Valid @RequestBody CreateNotificationDto dto) {
        NotificationResponseDto notification = notificationService.createNotification(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(notification);
    }

    // Lấy notifications với phân trang
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationResponseDto>> getNotifications(
            @PathVariable String userId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<NotificationResponseDto> notifications = notificationService.getNotifications(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    // Lấy notifications theo loại
    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<Page<NotificationResponseDto>> getNotificationsByType(
            @PathVariable String userId,
            @PathVariable NotificationType type,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<NotificationResponseDto> notifications = notificationService.getNotificationsByType(userId, type, pageable);
        return ResponseEntity.ok(notifications);
    }

    // Lấy notifications chưa đọc
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<Page<NotificationResponseDto>> getUnreadNotifications(
            @PathVariable String userId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<NotificationResponseDto> notifications = notificationService.getUnreadNotifications(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    // Đếm notifications chưa đọc
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable String userId) {
        Long count = notificationService.getUnreadCount(userId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);
        return ResponseEntity.ok(response);
    }

    // Đánh dấu đã đọc một notification
    @PutMapping("/user/{userId}/{notificationId}/read")
    public ResponseEntity<Map<String, String>> markAsRead(
            @PathVariable String userId,
            @PathVariable Long notificationId) {
        notificationService.markAsRead(userId, notificationId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification marked as read");
        return ResponseEntity.ok(response);
    }

    // Đánh dấu đã đọc nhiều notifications
    @PutMapping("/user/{userId}/read-multiple")
    public ResponseEntity<Map<String, String>> markMultipleAsRead(
            @PathVariable String userId,
            @Valid @RequestBody MarkNotificationsReadDto dto) {
        notificationService.markMultipleAsRead(userId, dto.getNotificationIds());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notifications marked as read");
        return ResponseEntity.ok(response);
    }

    // Đánh dấu tất cả đã đọc
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(@PathVariable String userId) {
        notificationService.markAllAsRead(userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "All notifications marked as read");
        return ResponseEntity.ok(response);
    }

    // Xóa notification
    @DeleteMapping("/user/{userId}/{notificationId}")
    public ResponseEntity<Map<String, String>> deleteNotification(
            @PathVariable String userId,
            @PathVariable Long notificationId) {
        notificationService.deleteNotification(userId, notificationId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Notification deleted successfully");
        return ResponseEntity.ok(response);
    }
}