package org.notification.dto.response;

import org.notification.domain.entity.Notification;
import org.notification.domain.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(String notificationId, String receiverId, String content, String url,boolean isRead, NotificationType type, LocalDateTime createdAt) {
    public static NotificationResponse toDto(Notification notification){
        return new NotificationResponse(
                notification.getId(),
                notification.getReceiverId(),
                notification.getContent(),
                notification.getUrl(),
                notification.isRead(),
                notification.getType(),
                notification.getCreatedAt()
        );

    }
}
