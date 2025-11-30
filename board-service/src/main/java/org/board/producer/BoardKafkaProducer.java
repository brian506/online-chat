package org.board.producer;

import lombok.RequiredArgsConstructor;
import org.common.event.CommentEvent;
import org.common.event.LikeEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardKafkaProducer {
    // 댓글, 좋아요 시 이벤트 발송

    @Value("${kafka.topic-config.comment.name}")
    private String commentTopic;

    @Value("${kafka.topic-config.like.name}")
    private String likeTopic;

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void sendCommentEvent(CommentEvent event){
        kafkaTemplate.send(commentTopic,event.commentId(),event);
    }

    public void sendLikeEvent(LikeEvent event){
        kafkaTemplate.send(likeTopic,event.boardId(),event);
    }

}
