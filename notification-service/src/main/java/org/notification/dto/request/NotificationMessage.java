package org.notification.dto.request;

import org.notification.domain.entity.Notification;
import org.notification.domain.entity.NotificationType;

public record NotificationMessage(String receiverId,
                                  String title,
                                  String body,
                                  String link,
                                  NotificationType type) {
}
