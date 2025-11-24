package org.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.request.CreateReadMessageEvent;
import org.chat.domain.dto.request.CreateRoomEvent;
import org.chat.domain.dto.response.*;
import org.chat.domain.entity.*;
import org.chat.domain.repository.MessageRepository;
import org.chat.domain.repository.RoomRepository;
import org.chat.domain.service.client.UserServiceClient;
import org.common.utils.ListUtil;
import org.common.utils.OptionalUtil;
import org.common.utils.SecurityUtil;
import org.common.utils.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final PublishService publishService;
    private final UserServiceClient userServiceClient;

    public RoomUserResponse createPrivateRoom(final String userId) {
        // 나와 상대 정보
        UserPrincipal loginUser = SecurityUtil.getCurrentUser();
        String roomKey = Room.generateRoomKey(loginUser.userId(),userId);


        Room room = findOrCreateRoom(loginUser,userId,roomKey);
        // 채팅방에서 보이는 닉네임(유저에 따라 다름)
        String view = getPeerNickname(loginUser.userId(), room);

        // 채팅방 발행
        CreateRoomEvent roomEvent = CreateRoomEvent.of(room.getId(), loginUser.userId(),userId);
        publishService.publishRoomCreated(roomEvent);
        return RoomUserResponse.of(room.getId(), view);
    }


    // 내 채팅방 목록
    @Transactional(readOnly = true)
    public RoomListResponse findRoomsByUsertype(final String userId, final String cursor) {
        List<Room> rooms = ListUtil.getOrElseThrowList(roomRepository.findRoomsByUserId(userId, cursor), "채팅방이 존재하지 않습니다.");
        List<RoomResponse> responses = rooms.stream()
                .map(room -> {
                    // 1. 요청자(userId) 관점에서 상대방 이름(닉네임 or 실명)을 계산
                    String displayPeerName = getPeerNickname(userId, room);
                    return Room.toRoomResponse(room, displayPeerName);
                })
                .toList();
        log.info("내 채팅방 목록 조회 #### " + responses);
        String nextCursor = rooms.get(rooms.size() - 1).getId(); // 페이지의 가장 마지막 커서 위치
        return new RoomListResponse(responses, nextCursor);
    }

    // 채팅방을 별명으로 검색(채팅방 목록에서)
    @Transactional(readOnly = true)
    public RoomResponse findRoomByName(final String peerName) {
        String myUserId = SecurityUtil.getCurrentUserId();
        Room room = OptionalUtil.getOrElseThrow(roomRepository.findRoomByParticipantId(myUserId, peerName), "존재하지 않는 채팅방입니다.");
        String displayPeerName = getPeerNickname(myUserId, room);
        return Room.toRoomResponse(room, displayPeerName);
    }

    // 메시지 읽음 표시
    public void updateToRead(final CreateReadMessageEvent event) {
        boolean isRead = roomRepository.updateLastReadMessageId(event.roomId(), event.userId(), event.messageId());
        MessageReadResponse response = null;
        if (isRead) {
            response = Room.toReadResponse(event);
            publishService.publishMessageRead(response);
        }
    }

    // 채팅방 삭제
    public void deleteRoom(final String roomId) {
        Room room = OptionalUtil.getOrElseThrow(roomRepository.findById(roomId), "존재하지 않는 채팅방입니다.");
        messageRepository.deleteByRoomId(roomId); // message 을 찾지 않고 바로 메시지 전부 삭제
        roomRepository.delete(room);
    }

    /**
     * 메서드 분리
     */

    private Room findOrCreateRoom(UserPrincipal loginUser, String targetUserId, String roomKey) {
        return roomRepository.findByRoomKey(roomKey)
                .orElseGet(() -> createNewRoom(loginUser, targetUserId, roomKey));
    }

    private Room createNewRoom(UserPrincipal loginUser, String targetUserId, String roomKey) {
        // 방이 없을 때만 외부 서비스 호출
        ChatUserResponse targetUser = userServiceClient.getUserInfo(targetUserId);
        Room newRoom = Room.ofPrivateRoom(Participant.me(loginUser),Participant.peer(targetUser),roomKey);
        return roomRepository.save(newRoom);
    }

    private String getPeerNickname(String myUserId, Room room) {
        return room.getParticipants().stream()
                .filter(p -> !p.userId().equals(myUserId))
                .findFirst()
                .map(Participant::nickname)
                .orElse("알 수 없는 사용자");
    }

}
