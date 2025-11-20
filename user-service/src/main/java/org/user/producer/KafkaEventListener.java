package org.user.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.user.domain.dto.event.FollowEvent;
import org.user.domain.dto.event.UserWhiskyFavoritesEvent;

@Component
@RequiredArgsConstructor
public class KafkaEventListener {

    /**
     * ApplicationEventPublisher 가 자동으로 리스너 클래스 메서드 실행
     */
    private final KafkaProducer producer;

    // 트랜잭션 COMMIT 후 Kafka 발행
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFavoritesEvent(UserWhiskyFavoritesEvent event){
        producer.sendFavoritesEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFollowingsEvent(FollowEvent event){
        producer.sendFollowingsEvent(event);
    }
}
