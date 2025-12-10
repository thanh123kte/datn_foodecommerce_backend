package com.example.qtifood.services;

import java.util.List;
import java.util.Map;

public interface FcmService {
    /**
     * Gửi thông báo FCM đến tất cả thiết bị của user
     * @param notificationType loại thông báo (ORDER, TOPUP, DELIVERY, REFUND, v.v...)
     */
    void sendNotification(String userId, String title, String body, String notificationType, Map<String, String> data);

    /**
     * Gửi FCM đến danh sách token
     */
    void sendToTokens(List<String> tokens, String title, String body, Map<String, String> data);
}
