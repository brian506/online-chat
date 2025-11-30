package org.notification.domain.consumer;


import lombok.RequiredArgsConstructor;
import org.common.event.CommentEvent;
import org.common.event.FollowEvent;
import org.common.event.LikeEvent;
import org.common.redis.FcmToken;
import org.common.redis.FcmTokenRepository;
import org.common.utils.ErrorMessages;
import org.common.utils.OptionalUtil;
import org.notification.domain.service.FcmService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotifyKafkaConsumer {

    private final FcmService fcmService;
    private final FcmTokenRepository fcmTokenRepository;

    // 팔로잉 이벤트 알림
    @KafkaListener(
            topics = "following-topic",
            groupId = "following-group",
            containerFactory = "followingKafkaListenerContainerFactory"
    )
    public void consumeFollowings(FollowEvent event) {
        // 팔로잉 당한 사람에게 알림(followingId)
        String title = "새로운 팔로우 알림";
        String body = event.followerNickname() + "님이 회원님을 팔로우하기 시작했습니다.";
        String link = "/v1/api/users/" + event.followerId();

        sendNotification(event.followingId(), title, body, link);
    }

    // 댓글 이벤트 알림
    @KafkaListener(
            topics = "comment-topic",
            groupId = "comment-group",
            containerFactory = "commentKafkaListenerContainerFactory"
    )
    public void consumeComment(CommentEvent event) {
        // 게시판 게시자에게 알림
        String title = "새로운 댓글 알림";
        String body = event.comment();
        String link = "/v1/api/boards/" + event.boardId();

        sendNotification(event.receiverId(), title, body, link);
    }

    // 좋아요 이벤트 알림
    @KafkaListener(
            topics = "like-topic",
            groupId = "like-group",
            containerFactory = "likeKafkaListenerContainerFactory"
    )
    public void consumeLike(LikeEvent event) {
        // 게시판 게시자에게 알림
        String title = "새로운 좋아요 알림";
        String body = event.nickname() + "님이 공감했습니다.";
        String link = "/v1/api/boards/" + event.boardId();

        sendNotification(event.receiverId(), title, body, link);
    }

    //todo 채팅 이벤트 알림





    private void sendNotification(String targetUserId, String title, String body, String link) {
        fcmTokenRepository.findById(targetUserId).ifPresent(fcmToken -> {
            fcmService.sendNotification(fcmToken.getToken(), title, body, link);
        });
    }
}
