package org.notification.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.notification.dto.request.NotificationMessage;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notifications")
public class Notification extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "notification_id", nullable = false, updatable = false)
    private String id;

    @Column(name = "receiver_id",nullable = false)
    private String receiverId;

    @Column(name = "content",nullable = false)
    private String content;

    @Column(name = "url",nullable = true)
    private String url;

    @Column(name = "isRead")
    private boolean isRead;

    @Column(name = "notification_type",nullable = false)
    private NotificationType type;

    public void read() {
        this.isRead = true;
    }

    public static Notification toEntity(NotificationMessage message){
        return Notification.builder()
                .receiverId(message.receiverId())
                .content(message.body())
                .url(message.link())
                .isRead(false)
                .type(message.type())
                .build();
    }
}
