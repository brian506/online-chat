package org.user.producer;

import lombok.RequiredArgsConstructor;
import org.common.event.FollowEvent;
import org.common.event.UserFavoritesWhiskyEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
@RequiredArgsConstructor
public class UserEventListener {

    /**
     * ApplicationEventPublisher 가 자동으로 리스너 클래스 메서드 실행
     */
    private final UserKafkaProducer producer;

    // 트랜잭션 COMMIT 후 Kafka 발행
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFavoritesEvent(UserFavoritesWhiskyEvent event){
        producer.sendFavoritesEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFollowingsEvent(FollowEvent event){
        producer.sendFollowingsEvent(event);
    }
}
