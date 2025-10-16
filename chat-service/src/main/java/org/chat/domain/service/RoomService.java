package org.chat.domain.service;

import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.response.Participant;
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

    public Room createOrGetRoom(String askerId, String answererId) {
        String roomKey = Room.directionalKey(askerId,answererId);

        return roomRepository.findByRoomKey(roomKey)
                .orElseGet(() -> {
                    ChatUser askerUser   = OptionalUtil.getOrElseThrow(userRepository.findById(askerId),    "존재하지 않는 사용자입니다.");
                    ChatUser answererUser = OptionalUtil.getOrElseThrow(userRepository.findById(answererId),"존재하지 않는 사용자입니다.");

                    // dto 변환
                    Participant asker    = Participant.asker(askerUser.getUserId(),   askerUser.getNickname(),   askerUser.getUsername());
                    Participant answerer = Participant.answerer(answererUser.getUserId(), answererUser.getNickname(), answererUser.getUsername());

                    Room room = Room.ofPrivateRoom(asker, answerer);
                    try {
                        return roomRepository.save(room);
                    } catch (DuplicateKeyException e) { // 채팅방 동시 생성 방지
                        return roomRepository.findByRoomKey(roomKey).orElseThrow(() -> e);
                    }
                });
    }

    // 요청자 관점에서 상대 표시 (ASKER=실명, ANSWERER=닉네임)
    private String viewByRole(String requesterUserId, Room room) {
        Participant askerUser = room.getParticipants().stream()
                .filter(p -> p.userId().equals(requesterUserId))
                .findFirst().orElseThrow();

        Participant answererUser = room.getParticipants().stream()
                .filter(p -> !p.userId().equals(requesterUserId))
                .findFirst().orElseThrow();

        if (askerUser.userType() == UserType.ASKER) return answererUser.username();
        if (askerUser.userType() == UserType.ANSWERER) return answererUser.nickname();
        return answererUser.nickname();
    }

    // 내 질문 목록

    // 내 답변 목록

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
