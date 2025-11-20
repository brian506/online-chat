package org.user.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.user.domain.dto.event.FollowEvent;
import org.user.domain.dto.event.UserWhiskyFavoritesEvent;
import org.user.domain.dto.response.WhiskyFavoritesResponse;

@Service
@RequiredArgsConstructor
public class KafkaProducer {


    @Value("${kafka.topic-config.user-favorites.name}")
    private String favoritesTopic;

    @Value("${kafka.topic-config.following.name}")
    private String followingTopic;

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void sendFavoritesEvent(UserWhiskyFavoritesEvent event) {
        kafkaTemplate.send(favoritesTopic,event.userId(),event);
    }

    public void sendFollowingsEvent(FollowEvent event){
        kafkaTemplate.send(followingTopic,event.followerId(),event);
    }

}
