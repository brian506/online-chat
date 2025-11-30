package org.notification.domain.repository.custom;

import org.notification.domain.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

public interface NotificationRepositoryCustom {
    Slice<Notification> getNotificationsByCursor(
            String userId,
            String cursorId,
            LocalDateTime cursorCreatedAt,
            Pageable pageable
    );
}
