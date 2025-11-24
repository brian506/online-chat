package org.chat.domain.repository;

import org.chat.domain.entity.Room;

import org.chat.domain.repository.customRepository.RoomRepositoryCustom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends MongoRepository<Room,String>, RoomRepositoryCustom {
    Optional<Room> findByParticipants(String userId);
    Optional<Room> findByRoomKey(String roomKey);
}
