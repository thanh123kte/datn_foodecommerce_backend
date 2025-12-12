package com.example.qtifood.repositories;

import com.example.qtifood.entities.Notification;
import com.example.qtifood.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Lấy notifications của user với phân trang
    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    // Lấy notifications của user theo loại
    Page<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(String userId, NotificationType type, Pageable pageable);

    // Lấy notifications chưa đọc của user
    Page<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(String userId, Pageable pageable);

    // Lấy tất cả notifications của user
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

    // Đếm notifications chưa đọc
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    Long countUnreadNotifications(@Param("userId") String userId);

    // Đếm notifications theo loại
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.type = :type")
    Long countNotificationsByType(@Param("userId") String userId, @Param("type") NotificationType type);

    // Đánh dấu đã đọc một notification
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notificationId AND n.user.id = :userId")
    int markAsRead(@Param("notificationId") Long notificationId, @Param("userId") String userId);

    // Đánh dấu đã đọc nhiều notifications
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id IN :notificationIds AND n.user.id = :userId")
    int markMultipleAsRead(@Param("notificationIds") List<Long> notificationIds, @Param("userId") String userId);

    // Đánh dấu tất cả đã đọc
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") String userId);

    // Xóa notifications cũ
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate")
    int deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Xóa tất cả notifications của user
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.user.id = :userId")
    int deleteAllByUserId(@Param("userId") String userId);

    // Tìm notifications theo entity
    List<Notification> findByEntityIdAndEntityType(Long entityId, String entityType);

    // Lấy notifications gần đây nhất
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("userId") String userId, Pageable pageable);
}