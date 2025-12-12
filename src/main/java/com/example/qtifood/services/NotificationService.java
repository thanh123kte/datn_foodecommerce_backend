package com.example.qtifood.services;

import com.example.qtifood.dtos.Notifications.CreateNotificationDto;
import com.example.qtifood.dtos.Notifications.NotificationResponseDto;
import com.example.qtifood.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface NotificationService {

    // Tạo notification mới
    NotificationResponseDto createNotification(String userId, CreateNotificationDto dto);

    // Lấy notifications với phân trang
    Page<NotificationResponseDto> getNotifications(String userId, Pageable pageable);

    // Lấy notifications theo loại
    Page<NotificationResponseDto> getNotificationsByType(String userId, NotificationType type, Pageable pageable);

    // Lấy notifications chưa đọc
    Page<NotificationResponseDto> getUnreadNotifications(String userId, Pageable pageable);

    // Lấy tất cả notifications
    List<NotificationResponseDto> getAllNotifications(String userId);

    // Lấy notifications gần đây
    List<NotificationResponseDto> getRecentNotifications(String userId, int limit);

    // Đánh dấu đã đọc một notification
    void markAsRead(String userId, Long notificationId);

    // Đánh dấu đã đọc nhiều notifications
    void markMultipleAsRead(String userId, List<Long> notificationIds);

    // Đánh dấu tất cả đã đọc
    void markAllAsRead(String userId);

    // Đếm notifications chưa đọc
    Long getUnreadCount(String userId);

    // Đếm notifications theo loại
    Long getCountByType(String userId, NotificationType type);

    // Thống kê notifications
    Map<String, Object> getNotificationStats(String userId);

    // Xóa notification
    void deleteNotification(String userId, Long notificationId);

    // Xóa tất cả notifications của user
    void deleteAllNotifications(String userId);

    // Admin: Dọn dẹp notifications cũ
    void cleanupOldNotifications(int daysOld);

    // Tạo notification cho order
    void createOrderNotification(String userId, Long orderId, String title, String message, NotificationType type);

    // Tạo notification cho store
    void createStoreNotification(String userId, Long storeId, String title, String message);

    // Tạo notification hệ thống
    void createSystemNotification(String userId, String title, String message);
}