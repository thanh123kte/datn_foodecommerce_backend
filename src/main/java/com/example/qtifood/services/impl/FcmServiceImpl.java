package com.example.qtifood.services.impl;

import com.example.qtifood.repositories.DeviceTokenRepository;
import com.example.qtifood.services.FcmService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {
    private static final Logger log = LoggerFactory.getLogger(FcmServiceImpl.class);
    private final DeviceTokenRepository deviceTokenRepository;

    @PostConstruct
    public void initFirebase() {
        // FirebaseApp initialization should be done in a config class, not here
        // Example:
        // FileInputStream serviceAccount = new FileInputStream("serviceAccountKey.json");
        // FirebaseOptions options = FirebaseOptions.builder()
        //     .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        //     .build();
        // if (FirebaseApp.getApps().isEmpty()) {
        //     FirebaseApp.initializeApp(options);
        // }
    }

    @Override
    public void sendOrderNotification(String userId, String title, String body, Map<String, String> data) {
        List<String> tokens = deviceTokenRepository.findByUserId(userId)
            .stream().map(dt -> dt.getToken()).toList();
        log.info("[FcmService] Chuẩn bị gửi FCM cho userId={}, tokens={}, title={}, body={}, data={}", userId, tokens, title, body, data);
        sendToTokens(tokens, title, body, data);
    }

    @Override
    public void sendToTokens(List<String> tokens, String title, String body, Map<String, String> data) {
        if (tokens == null || tokens.isEmpty()) return;

        for (String token : tokens) {
            try {
                Message message = Message.builder()
                    .setToken(token)
                    // Notification hiển thị nền
                    .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                    .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .setSound("notification_sound")          // không kèm .mp3
                            .setChannelId("order_updates")           // giúp dùng đúng sound
                            .setPriority(AndroidNotification.Priority.HIGH)
                            .build())
                        .build())
                    .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder().setSound("notification_sound.caf").build())
                        .build())
                    // Data để app điều hướng
                    .putAllData(data != null ? data : Map.of())
                    .build();

                FirebaseMessaging.getInstance().sendAsync(message);
                log.info("[FcmService] Sent FCM to token={}, title={}, body={}", token, title, body);
            } catch (Exception e) {
                log.error("[FcmService] Send FCM failed token={}: {}", token, e.getMessage());
            }
        }
    }

    /**
     * Gửi FCM tới danh sách token với sound tùy chỉnh
     * @param soundName tên file sound (ví dụ: "notification_sound" - không bao gồm .mp3)
     */
    public void sendToTokens(List<String> tokens, String title, String body, Map<String, String> data, String soundName) {
        for (String token : tokens) {
            try {
                log.info("[FcmService] Gửi FCM tới token={}, title={}, body={}, data={}, sound={}", token, title, body, data, soundName);
                
                // Android Notification với sound
                AndroidNotification androidNotification = AndroidNotification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .setSound(soundName)
                    .setPriority(AndroidNotification.Priority.HIGH)
                    .build();

                // iOS Notification với sound
                Aps aps = Aps.builder()
                    .setSound(soundName + ".caf")
                    .build();
                ApnsConfig apnsConfig = ApnsConfig.builder()
                    .setAps(aps)
                    .build();

                Message.Builder builder = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                    .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(androidNotification)
                        .build())
                    .setApnsConfig(apnsConfig);

                if (data != null && !data.isEmpty()) {
                    builder.putAllData(data);
                }
                Message message = builder.build();
                FirebaseMessaging.getInstance().sendAsync(message);
                log.info("[FcmService] Đã gửi FCM thành công tới token={}", token);
            } catch (Exception e) {
                log.error("[FcmService] Lỗi gửi FCM tới token={}: {}", token, e.getMessage());
            }
        }
    }
}
