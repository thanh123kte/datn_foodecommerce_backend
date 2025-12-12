package com.example.qtifood.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    @PostConstruct
    public void init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://datn-foodecommerce-default-rtdb.asia-southeast1.firebasedatabase.app")
                        .build();
                FirebaseApp.initializeApp(options);
                System.out.println("FirebaseApp initialized successfully with Realtime Database");
            }
        } catch (Exception e) {
            System.err.println("FirebaseApp initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
