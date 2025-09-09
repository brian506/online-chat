package org.chat.domain.repository;

import org.chat.domain.entity.Message;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MessageRepository extends ReactiveMongoRepository<Message,String> {
    // 특정 채팅방의 메시지를 시간순으로 찾아 Flux<ChatMessage> 형태로 반환
    Flux<Message> findByRoomIdOrderByTimestamp(String roomId);
}

