package org.chat.domain.repository.customRepository;


import org.chat.domain.entity.Room;


import java.util.List;
import java.util.Optional;


public interface RoomRepositoryCustom {
     List<Room> findRoomsByUserId(String userId, String cursor);
     Optional<Room> findRoomByParticipantId(String userId,String peerId);
     boolean updateLastReadMessageId(String roomId,String userId, String messageId);
}
