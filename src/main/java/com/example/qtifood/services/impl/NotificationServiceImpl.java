package com.example.qtifood.services.impl;

import com.example.qtifood.dtos.Notifications.CreateNotificationDto;
import com.example.qtifood.dtos.Notifications.NotificationResponseDto;
import com.example.qtifood.entities.Notification;
import com.example.qtifood.entities.User;
import com.example.qtifood.enums.NotificationType;
import com.example.qtifood.exceptions.ResourceNotFoundException;
import com.example.qtifood.mappers.NotificationMapper;
import com.example.qtifood.repositories.NotificationRepository;
import com.example.qtifood.repositories.UserRepository;
import com.example.qtifood.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public NotificationResponseDto createNotification(String userId, CreateNotificationDto dto) {
        log.info("Creating notification for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Notification notification = notificationMapper.toEntity(dto, user);
        Notification savedNotification = notificationRepository.save(notification);
        
        log.info("Notification created with id: {}", savedNotification.getId());
        return notificationMapper.toResponseDto(savedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponseDto> getNotifications(String userId, Pageable pageable) {
        log.debug("Getting notifications for user: {} with pageable: {}", userId, pageable);
        
        Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(notificationMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponseDto> getNotificationsByType(String userId, NotificationType type, Pageable pageable) {
        log.debug("Getting notifications for user: {} with type: {}", userId, type);
        
        Page<Notification> notifications = notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type, pageable);
        return notifications.map(notificationMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponseDto> getUnreadNotifications(String userId, Pageable pageable) {
        log.debug("Getting unread notifications for user: {}", userId);
        
        Page<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable);
        return notifications.map(notificationMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getAllNotifications(String userId) {
        log.debug("Getting all notifications for user: {}", userId);
        
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(notificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getRecentNotifications(String userId, int limit) {
        log.debug("Getting recent {} notifications for user: {}", limit, userId);
        
        Pageable pageable = PageRequest.of(0, limit);
        List<Notification> notifications = notificationRepository.findRecentNotifications(userId, pageable);
        return notifications.stream()
                .map(notificationMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(String userId, Long notificationId) {
        log.info("Marking notification {} as read for user: {}", notificationId, userId);
        
        int updated = notificationRepository.markAsRead(notificationId, userId);
        if (updated == 0) {
            throw new ResourceNotFoundException("Notification not found or access denied");
        }
    }

    @Override
    public void markMultipleAsRead(String userId, List<Long> notificationIds) {
        log.info("Marking {} notifications as read for user: {}", notificationIds.size(), userId);
        
        int updated = notificationRepository.markMultipleAsRead(notificationIds, userId);
        log.info("Marked {} notifications as read", updated);
    }

    @Override
    public void markAllAsRead(String userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        
        int updated = notificationRepository.markAllAsRead(userId);
        log.info("Marked {} notifications as read", updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(String userId) {
        return notificationRepository.countUnreadNotifications(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCountByType(String userId, NotificationType type) {
        return notificationRepository.countNotificationsByType(userId, type);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getNotificationStats(String userId) {
        log.debug("Getting notification stats for user: {}", userId);
        
        Map<String, Object> stats = new HashMap<>();
        
        // Tổng số notifications
        Long totalCount = (long) notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).size();
        stats.put("totalCount", totalCount);
        
        // Số notifications chưa đọc
        Long unreadCount = getUnreadCount(userId);
        stats.put("unreadCount", unreadCount);
        
        // Thống kê theo loại
        Map<String, Long> typeStats = new HashMap<>();
        for (NotificationType type : NotificationType.values()) {
            Long count = getCountByType(userId, type);
            typeStats.put(type.name(), count);
        }
        stats.put("typeStats", typeStats);
        
        return stats;
    }

    @Override
    public void deleteNotification(String userId, Long notificationId) {
        log.info("Deleting notification {} for user: {}", notificationId, userId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Access denied");
        }
        
        notificationRepository.delete(notification);
    }

    @Override
    public void deleteAllNotifications(String userId) {
        log.info("Deleting all notifications for user: {}", userId);
        
        int deleted = notificationRepository.deleteAllByUserId(userId);
        log.info("Deleted {} notifications", deleted);
    }

    @Override
    public void cleanupOldNotifications(int daysOld) {
        log.info("Cleaning up notifications older than {} days", daysOld);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        int deleted = notificationRepository.deleteOldNotifications(cutoffDate);
        
        log.info("Deleted {} old notifications", deleted);
    }

    @Override
    public void createOrderNotification(String userId, Long orderId, String title, String message, NotificationType type) {
        log.info("Creating order notification for user: {}, order: {}", userId, orderId);
        
        CreateNotificationDto dto = new CreateNotificationDto();
        dto.setTitle(title);
        dto.setMessage(message);
        dto.setType(type);
        dto.setEntityId(orderId);
        dto.setEntityType("ORDER");
        
        createNotification(userId, dto);
    }

    @Override
    public void createStoreNotification(String userId, Long storeId, String title, String message) {
        log.info("Creating store notification for user: {}, store: {}", userId, storeId);
        
        CreateNotificationDto dto = new CreateNotificationDto();
        dto.setTitle(title);
        dto.setMessage(message);
        dto.setType(NotificationType.SYSTEM);
        dto.setEntityId(storeId);
        dto.setEntityType("STORE");
        
        createNotification(userId, dto);
    }

    @Override
    public void createSystemNotification(String userId, String title, String message) {
        log.info("Creating system notification for user: {}", userId);
        
        CreateNotificationDto dto = new CreateNotificationDto();
        dto.setTitle(title);
        dto.setMessage(message);
        dto.setType(NotificationType.SYSTEM);
        
        createNotification(userId, dto);
    }
}