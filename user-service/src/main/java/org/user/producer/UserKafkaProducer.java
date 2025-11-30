package org.user.producer;

import lombok.RequiredArgsConstructor;
import org.common.event.FollowEvent;
import org.common.event.UserFavoritesWhiskyEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserKafkaProducer {

    @Value("${kafka.topic-config.user-favorites.name}")
    private String favoritesTopic;

    @Value("${kafka.topic-config.following.name}")
    private String followingTopic;

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void sendFavoritesEvent(UserFavoritesWhiskyEvent event) {
        kafkaTemplate.send(favoritesTopic,event.userId(),event);
    }

    public void sendFollowingsEvent(FollowEvent event){
        kafkaTemplate.send(followingTopic,event.followerId(),event);
    }

}
