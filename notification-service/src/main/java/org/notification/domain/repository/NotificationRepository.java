package org.notification.domain.repository;

import org.notification.domain.entity.Notification;
import org.notification.domain.repository.custom.NotificationRepositoryCustom;
import org.notification.dto.response.NotificationResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,String> , NotificationRepositoryCustom {
    long countByReceiverIdAndIsReadFalse(String receiverId);
    Optional<Notification> findByIdAndReceiverId(String id, String receiverId);
}
