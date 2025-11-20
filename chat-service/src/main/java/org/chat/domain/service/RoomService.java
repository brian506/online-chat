package org.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.request.CreateReadMessageEvent;
import org.chat.domain.dto.request.CreateRoomEvent;
import org.chat.domain.dto.response.*;
import org.chat.domain.entity.*;
import org.chat.domain.repository.MessageRepository;
import org.chat.domain.repository.RoomRepository;
import org.chat.domain.service.client.BoardServiceClient;
import org.common.exception.custom.DataNotFoundException;
import org.common.utils.ListUtil;
import org.common.utils.OptionalUtil;
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
    private final BoardServiceClient boardServiceClient;

//    public RoomUserResponse createPrivateRoom(final String userId) {
//
//        String view = viewByRole(boardResponse.askerId(), room);
//
//        // 채팅방 발행
//        CreateRoomEvent roomEvent = CreateRoomEvent.of(room.getId(), boardResponse.askerId(), boardResponse.answererId());
//        publishService.publishRoomCreated(roomEvent);
//        return RoomUserResponse.of(room.getId(), view);
//    }


    // 내 채팅방 목록
    @Transactional(readOnly = true)
    public RoomListResponse findRoomsByUsertype(final String userId, final UserType userType, final String cursor){
        List<Room> rooms = ListUtil.getOrElseThrowList(roomRepository.findRoomsByUserAndType(userId,userType,cursor),"채팅방이 존재하지 않습니다.");
        List<RoomResponse> responses = rooms.stream()
                .map(room -> {
                    // 1. 요청자(userId) 관점에서 상대방 이름(닉네임 or 실명)을 계산
                    String displayPeerName = viewByRole(userId, room);
                    return Room.toRoomResponse(room, displayPeerName);
                })
                .toList();
        log.info("내 채팅방 목록 조회 #### " + responses);
        String nextCursor =  rooms.get(rooms.size() - 1).getId(); // 페이지의 가장 마지막 커서 위치
        return new RoomListResponse(responses, nextCursor);
    }

    // 채팅방을 별명으로 검색(채팅방 목록에서)
    @Transactional(readOnly = true)
    public RoomResponse findRoomByName(final String myUserId, final String peerName){
        Room room = OptionalUtil.getOrElseThrow(roomRepository.findRoomByParticipantId(myUserId,peerName),"존재하지 않는 채팅방입니다.");
        String displayPeerName = viewByRole(myUserId,room);
        return Room.toRoomResponse(room,displayPeerName);
    }

    // 메시지 읽음 표시
    public void updateToRead(final CreateReadMessageEvent event) {
        boolean isRead = roomRepository.updateLastReadMessageId(event.roomId(), event.userId(), event.messageId());
        MessageReadResponse response = null;
        if(isRead){
            response = Room.toReadResponse(event);
            publishService.publishMessageRead(response);
        }
    }

    // 채팅방 삭제
    public void deleteRoom(final String roomId){
        Room room = OptionalUtil.getOrElseThrow(roomRepository.findById(roomId),"존재하지 않는 채팅방입니다.");
        messageRepository.deleteByRoomId(roomId); // message 을 찾지 않고 바로 메시지 전부 삭제
        roomRepository.delete(room);
    }

    /**
     * 메서드 분리
     */
    public Room createOrGetRoom(AnswerFromBoardResponse response) {


            // dto 변환
            Participant asker    = Participant.asker(response.askerId(),response.askerNickname());
            Participant answerer = Participant.answerer(response.answerId(),response.answererNickname());

            Room newRoom = Room.ofPrivateRoom(response, asker, answerer);
            return roomRepository.save(newRoom);

    }

    // 요청자 관점에서 상대 표시 (ASKER=실명, ANSWERER=닉네임)
    // 채팅방 안에서 참여자들 중에 내가 질문자인지, 답변자인지에 따른 Participant 객체 반환
    private String viewByRole(String requesterUserId, Room room) {

        Participant askerUser = room.getParticipants().stream()
                .filter(p -> p.userId().equals(requesterUserId))
                .findFirst().orElseThrow(() -> new DataNotFoundException("채팅방 참여자 정보가 없습니다."));

        Participant answererUser = room.getParticipants().stream()
                .filter(p -> !p.userId().equals(requesterUserId))
                .findFirst().orElseThrow(() -> new DataNotFoundException("상대방 정보가 없습니다."));

        if (askerUser.userType() == UserType.ASKER) {
            return answererUser.nickname();
        }
        return answererUser.nickname();
    }

}
