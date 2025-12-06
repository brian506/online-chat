package org.chat.handler;

import lombok.RequiredArgsConstructor;
import org.chat.domain.service.ChatRoomUserRedisService;
import org.chat.producer.ChatKafkaProducer;
import org.common.event.MessageEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatEventListener {

    private final ChatKafkaProducer producer;
    private final ChatRoomUserRedisService redisService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessageEvent(MessageEvent event){
        if(!redisService.isUserConnected(event.roomId(), event.receiverId())){
            producer.sendChatEvent(event); // 사용자가 채팅방에 접속하지 않을때만 카프카 발행
        }
    }
}
