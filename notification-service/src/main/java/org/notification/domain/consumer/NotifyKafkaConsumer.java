package org.notification.domain.consumer;


import lombok.RequiredArgsConstructor;
import org.common.event.CommentEvent;
import org.common.event.FollowEvent;
import org.common.event.LikeEvent;
import org.common.redis.FcmToken;
import org.common.redis.FcmTokenRepository;
import org.common.utils.ErrorMessages;
import org.common.utils.OptionalUtil;
import org.notification.domain.entity.Notification;
import org.notification.domain.entity.NotificationType;
import org.notification.domain.repository.NotificationRepository;
import org.notification.domain.service.FcmService;
import org.notification.dto.request.NotificationMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotifyKafkaConsumer {

    private final FcmService fcmService;
    private final FcmTokenRepository fcmTokenRepository;
    private final NotificationRepository notificationRepository;

    // 팔로잉 이벤트 알림
    @KafkaListener(
            topics = "following-topic",
            groupId = "following-group",
            containerFactory = "followingKafkaListenerContainerFactory"
    )
    public void consumeFollowings(FollowEvent event) {
        // 팔로잉 당한 사람에게 알림(followingId)
        NotificationMessage message = new NotificationMessage(
                event.followingId(),
                "새로운 팔로우 알림",
                event.followerNickname() + "님이 회원님을 팔로우하기 시작했습니다.",
                "/v1/api/users/" + event.followerId(),
                NotificationType.FOLLOWING
        );
        processNotification(message);
    }

    // 댓글 이벤트 알림
    @KafkaListener(
            topics = "comment-topic",
            groupId = "comment-group",
            containerFactory = "commentKafkaListenerContainerFactory"
    )
    public void consumeComment(CommentEvent event) {
        // 게시판 게시자에게 알림
        NotificationMessage message = new NotificationMessage(
                event.receiverId(),
                "새로운 댓글 알림",
                event.comment(),
                "/v1/api/boards/" + event.boardId(),
                NotificationType.COMMENT
        );
        processNotification(message);
    }

    // 좋아요 이벤트 알림
    @KafkaListener(
            topics = "like-topic",
            groupId = "like-group",
            containerFactory = "likeKafkaListenerContainerFactory"
    )
    public void consumeLike(LikeEvent event) {
        // 게시판 게시자에게 알림
        NotificationMessage message = new NotificationMessage(
                event.receiverId(),
                "새로운 좋아요 알림",
                event.nickname() + "님이 공감했습니다.",
                "/v1/api/boards/" + event.boardId(),
                NotificationType.LIKE
        );

        processNotification(message);
    }

    //todo 채팅 이벤트 알림


    /**
     * 중복 메서드 분리
     */
    private void processNotification(NotificationMessage message) {
        saveNotification(message);
        sendFcm(message);
    }
    private void saveNotification(NotificationMessage message){
        notificationRepository.save(Notification.toEntity(message));
    }
    private void sendFcm(NotificationMessage message) {
        fcmTokenRepository.findById(message.receiverId()).ifPresent(fcmToken -> {
            fcmService.sendNotification(
                    fcmToken.getToken(),
                    message.title(),
                    message.body(),
                    message.link()
            );
        });
    }
}
