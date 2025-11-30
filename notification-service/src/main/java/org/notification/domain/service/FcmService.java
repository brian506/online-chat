package org.notification.domain.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.notification.domain.dto.FcmSendRequest;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class FcmService {

    public void sendNotification(String token, String title, String body, String link){

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token) // 받는 사람 토큰
                .setNotification(notification) // 화면에 보일 내용
                .putData("url", link) // 알림 발생 페이지
                .build();
        try {
            FirebaseMessaging.getInstance().send(message);
            log.info("FCM 전송 성공");
        } catch (Exception e) {
            log.error("FCM 전송 실패 (토큰 만료 가능성): {}", e.getMessage());
        }
    }
}
