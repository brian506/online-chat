package org.notification.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.common.event.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class NotifyKafkaConsumerConfig {

    @Value("${kafka.consumer.following-group.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.following-group.group-id}")
    private String followingGroupId;

    @Value("${kafka.consumer.chatting-group.group-id}")
    private String chattingGroupId;

    @Value("${kafka.consumer.comment-group.group-id}")
    private String commentGroupId;

    @Value("${kafka.consumer.like-group.group-id}")
    private String likeGroupId;

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> createContainerFactory(Class<T> eventType, String groupId) {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        JsonDeserializer<T> deserializer = new JsonDeserializer<>(eventType);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        ConsumerFactory<String, T> consumerFactory = new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );

        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3); // 병렬 처리 개수
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // 수동 커밋 모드

        return factory;
    }

    @Bean("chattingKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, SendMessageEvent> chattingListenerContainerFactory() {
        return createContainerFactory(SendMessageEvent.class, chattingGroupId);
    }

    @Bean("followingKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, FollowEvent> followingListenerContainerFactory() {
        return createContainerFactory(FollowEvent.class, followingGroupId);
    }

    @Bean("commentKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, CommentEvent> commentListenerContainerFactory() {
        return createContainerFactory(CommentEvent.class, commentGroupId);
    }

    @Bean("likeKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, LikeEvent> likeListenerContainerFactory() {
        return createContainerFactory(LikeEvent.class, likeGroupId);
    }
}
