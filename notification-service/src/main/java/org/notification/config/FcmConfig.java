package org.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class FcmConfig {

    @Value("classpath:whisky-firebase-service-account.json")
    private Resource googleCredentials;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // 이미 초기화되어 있으면 기존 것 반환 (중복 초기화 방지)
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(googleCredentials.getInputStream()))
                .build();

        return FirebaseApp.initializeApp(options);
    }

}
