package org.board.consumer;

import lombok.RequiredArgsConstructor;
import org.common.event.FollowEvent;
import org.common.event.UserFavoritesWhiskyEvent;
import org.board.domain.entity.BoardUserFollow;
import org.board.domain.entity.UserWhiskyFavorites;
import org.board.domain.repository.UserBoardFollowRepository;
import org.board.domain.repository.UserWhiskyFavoritesRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.Acknowledgment;

@Component
@RequiredArgsConstructor
public class BoardKafkaConsumer {

    private final UserWhiskyFavoritesRepository favoritesRepository;
    private final UserBoardFollowRepository followRepository;

    @KafkaListener(
            topics = "following-topic",
            groupId = "following-group",
            containerFactory = "followingKafkaListenerContainerFactory"
    )
    public void handleFollowings(FollowEvent event,Acknowledgment ack){

        switch (event.actionType()) {
            case ADD -> { // 팔로잉 추가 이벤트
                followRepository.save(BoardUserFollow.toEntity(event));
                ack.acknowledge();
            }
            case REMOVE -> { // 팔로잉 해제 이벤트
                followRepository.deleteByFollowingIdAndFollowerId(event.followingId(), event.followerId());
                ack.acknowledge();
            }
        }
    }

    @KafkaListener(
            topics = "favorites-topic",
            groupId = "favorites-group",
            containerFactory = "userFavoritesKafkaListenerContainerFactory"
    )
    public void handleFavorites(UserFavoritesWhiskyEvent event, Acknowledgment ack){

        switch (event.actionType()) {
            case ADD -> { // 즐겨찾기 추가 이벤트
                favoritesRepository.save(UserWhiskyFavorites.toEntity(event));
                ack.acknowledge();
            }
            case REMOVE -> { // 즐겨찾기 해제 이벤트
                favoritesRepository.deleteByUserIdAndWhiskyId(event.userId(), event.whiskyId());
                ack.acknowledge();
            }
        }

    }
}
