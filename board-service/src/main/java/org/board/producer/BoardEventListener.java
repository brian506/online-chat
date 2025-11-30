package org.board.producer;

import lombok.RequiredArgsConstructor;
import org.common.event.CommentEvent;
import org.common.event.LikeEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BoardEventListener {

    private final BoardKafkaProducer producer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentEvent(CommentEvent event){
        producer.sendCommentEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLikeEvent(LikeEvent event){
        producer.sendLikeEvent(event);
    }
}
