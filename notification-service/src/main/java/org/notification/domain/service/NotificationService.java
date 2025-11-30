package org.notification.domain.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.common.utils.ErrorMessages;
import org.common.utils.OptionalUtil;
import org.common.utils.SecurityUtil;
import org.notification.domain.entity.Notification;
import org.notification.domain.repository.NotificationRepository;
import org.notification.dto.response.NotificationResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 전체 알림 조회
     */
    @Transactional(readOnly = true)
    public Slice<NotificationResponse> getMyNotifications(
            String cursorId,
            LocalDateTime cursorCreatedAt,
            Pageable pageable
    ) {
        String loginUserId = SecurityUtil.getCurrentUserId();
        Slice<Notification> notifications = notificationRepository.getNotificationsByCursor(loginUserId,cursorId,cursorCreatedAt,pageable);
        return notifications.map(NotificationResponse::toDto);
    }

    /**
     * 알림 읽음 처리 (단건)
     */
    @Transactional
    public void readNotification(String notificationId) {
        String loginUserId = SecurityUtil.getCurrentUserId();
        Notification notification = OptionalUtil.getOrElseThrow(notificationRepository.findByIdAndReceiverId(notificationId,loginUserId),ErrorMessages.NOTIFICATION_NOT_FOUND);
        notification.read();
    }

    /**
     *  알림 삭제 (단건)
     */
    @Transactional
    public void deleteNotification(String notificationId) {
        String loginUserId = SecurityUtil.getCurrentUserId();
        Notification notification = OptionalUtil.getOrElseThrow(notificationRepository.findByIdAndReceiverId(notificationId,loginUserId),ErrorMessages.NOTIFICATION_NOT_FOUND);
        notificationRepository.delete(notification);
    }

    /**
     *  읽지 않은 알림 개수 조회
     */
    @Transactional(readOnly = true)
    public long countUnreadNotifications(String userId) {
        return notificationRepository.countByReceiverIdAndIsReadFalse(userId);
    }

}
