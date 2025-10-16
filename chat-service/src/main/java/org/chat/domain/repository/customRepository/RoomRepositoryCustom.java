package org.chat.domain.repository.customRepository;


import org.chat.domain.entity.Room;
import org.chat.domain.entity.UserType;



import java.util.List;


public interface RoomRepositoryCustom {
     List<Room> findRoomsByUserAndType(String userId, UserType userType, String cursor);
}
