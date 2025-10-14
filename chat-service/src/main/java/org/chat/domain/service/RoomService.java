package org.chat.domain.service;

import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.Participants;
import org.chat.domain.dto.response.RoomResponse;
import org.chat.domain.dto.response.RoomUserResponse;
import org.chat.domain.entity.ChatUser;
import org.chat.domain.entity.Room;
import org.chat.domain.entity.UserType;
import org.chat.domain.repository.ChatUserRepository;
import org.chat.domain.repository.RoomRepository;
import org.common.utils.OptionalUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final ChatUserRepository userRepository;


    public RoomUserResponse createRoom(String myUserId, String targetUserId) {
        Room room = createOrGetRoom(myUserId, targetUserId);
        String view = viewByRole(myUserId, room);
        return RoomUserResponse.of(room.getId(), view);
    }

    public Room createOrGetRoom(String myId, String targetId) {
        //  키 한 번만 계산
         String roomKey = Room.buildRoomKey(myId, targetId);

        // 1) 기존 방 있으면 바로 리턴
        var existing = roomRepository.findByRoomKey(roomKey);
        if (existing.isPresent()) {
            return existing.get();
        }

        // 2) 없으면 생성
        ChatUser me = OptionalUtil.getOrElseThrow(userRepository.findById(myId),"존재하지 않는 사용자입니다.");
        ChatUser peer = OptionalUtil.getOrElseThrow(userRepository.findById(targetId),"존재하지 않는 사용자입니다.");

        Participants asker = Participants.asker(me.getUserId(), me.getNickname(), me.getUsername());
        Participants answerer = Participants.answerer(peer.getUserId(),peer.getNickname(),peer.getUsername());

        Room room = Room.of(asker, answerer);

        try {

            return roomRepository.save(room);
        } catch (DuplicateKeyException e) {
            // 4) 동시 생성 레이스: 유니크 충돌 시 재조회하여 리턴
            return roomRepository.findByRoomKey(roomKey)
                    .orElseThrow(() -> e); // 이 경우 거의 무조건 존재
        }
    }

    // 요청자 관점에서 상대 표시 (ASKER=실명, ANSWERER=닉네임)
    private String viewByRole(String requesterUserId, Room room) {
        Participants me = room.getParticipants().stream()
                .filter(p -> p.userId().equals(requesterUserId))
                .findFirst().orElseThrow();

        Participants peer = room.getParticipants().stream()
                .filter(p -> !p.userId().equals(requesterUserId))
                .findFirst().orElseThrow();

        if (me.userType() == UserType.ASKER) return peer.realName();
        if (me.userType() == UserType.ANSWERER) return peer.nickname();
        return peer.nickname();
    }



//    public List<RoomResponse> findAllRooms(){
//        List<Room> room = ListUtil.getOrElseThrowList(roomRepository.findAll(),"채팅방이 아직 생성되지 않았습니다.");
//        return room.stream()
//                .map(RoomResponse::toDto)
//                .collect(Collectors.toList());
//    }

    // 채팅방을 별명으로 검색(채팅방 목록에서)
    public RoomResponse findRoomByName(final String nickname){
        ChatUser user = OptionalUtil.getOrElseThrow(userRepository.findByNickname(nickname),"존재하지 않는 익명의 사용자입니다.");
        Room room = OptionalUtil.getOrElseThrow(roomRepository.findByName(user.getNickname()),"존재하지 않는 채팅방입니다.");
        return Room.toDto(room);
    }

    // 채팅방 삭제
    public void deleteRoom(final String roomId){
        Room room = OptionalUtil.getOrElseThrow(roomRepository.findById(roomId),"존재하지 않는 채팅방입니다.");
        roomRepository.delete(room);
    }



}
