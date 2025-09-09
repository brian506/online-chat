package org.chat.domain.repository;

import org.chat.domain.entity.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room,String> {
}
