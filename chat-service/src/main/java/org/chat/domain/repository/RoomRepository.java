package org.chat.domain.repository;

import org.chat.domain.entity.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends MongoRepository<Room,String> {
    Optional<Room> findByName(String name);
    Optional<Room> findByRoomKey(String roomKey);

}
