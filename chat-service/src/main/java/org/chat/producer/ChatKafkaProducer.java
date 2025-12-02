package org.chat.producer;

import lombok.RequiredArgsConstructor;
import org.common.event.MessageEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatKafkaProducer {

    @Value("${kafka.admin.properties.bootstrap.servers}")
    private String chatTopic;

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void sendChatEvent(MessageEvent event){
        kafkaTemplate.send(chatTopic,event.roomId(),event);
    }

}
