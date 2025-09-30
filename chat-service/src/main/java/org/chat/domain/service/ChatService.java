package org.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.request.CreateRoomRequest;
import org.chat.domain.dto.response.RoomResponse;
import org.chat.domain.entity.Room;
import org.chat.domain.repository.RoomRepository;
import org.common.utils.ListUtil;
import org.common.utils.OptionalUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    private final RoomRepository roomRepository;

    public Room createRoom(CreateRoomRequest request){
        Room newRoom = Room.toEntity(request);
        return roomRepository.save(newRoom);
    }

    public List<RoomResponse> findAllRooms(){
        List<Room> room = ListUtil.getOrElseThrowList(roomRepository.findAll(),"채팅방이 아직 생성되지 않았습니다.");
        return room.stream()
                .map(RoomResponse::toDto)
                .collect(Collectors.toList());
    }

    public RoomResponse findRoom(String id){
        Room room = OptionalUtil.getOrElseThrow(roomRepository.findById(id),"존재하지 않는 채팅방입니다.");
        return RoomResponse.toDto(room);
    }
}
