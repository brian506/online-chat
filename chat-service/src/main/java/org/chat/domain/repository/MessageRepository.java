package org.chat.domain.repository;

import org.chat.domain.entity.Message;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<Message,String> {

    Optional<Message> findByRoomIdOrderByTimestamp(String roomId);
}

