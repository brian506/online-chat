package org.board.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.board.domain.dto.event.FollowEvent;
import org.board.domain.dto.event.UserFavoritesEvent;
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
public class BoardKafkaConsumerConfig {
    @Value("${kafka.consumer.favorites-group.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.favorites-group.group-id}")
    private String favoritesGroupId;

    @Value("${kafka.consumer.following-group.group-id}")
    private String followingGroupId;

    @Bean("userFavoritesConsumerConfigs")
    public Map<String, Object> consumerFavoritesConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, favoritesGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return props;
    }

    @Bean("followingConsumerConfigs")
    public Map<String, Object> consumerFollowingConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, favoritesGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return props;
    }

    @Bean("userFavoritesConsumerFactory")
    public ConsumerFactory<String, UserFavoritesEvent> consumerFavoritesFactory() {
        JsonDeserializer<UserFavoritesEvent> deserializer = new JsonDeserializer<>(UserFavoritesEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(
                consumerFavoritesConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean("followingConsumerFactory")
    public ConsumerFactory<String, FollowEvent> consumerFollowFactory() {
        JsonDeserializer<FollowEvent> deserializer = new JsonDeserializer<>(FollowEvent.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(
                consumerFollowingConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean("userFavoritesKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, UserFavoritesEvent> favoritesListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserFavoritesEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFavoritesFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    @Bean("followingKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String,FollowEvent> followingListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String,FollowEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFollowFactory());
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}
