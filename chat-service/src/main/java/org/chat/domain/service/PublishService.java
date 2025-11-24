package org.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.request.CreateReadMessageEvent;
import org.chat.domain.dto.request.CreateRoomEvent;
import org.chat.domain.dto.request.SendMessageEvent;
import org.chat.domain.dto.response.MessageBroadcastResponse;
import org.chat.domain.dto.response.MessageReadResponse;
import org.chat.domain.entity.Room;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PublishService {

    private final SimpMessagingTemplate messagingTemplate;
    private final String MESSAGE_DEST_URL = "/topic/chat/room/";
    private final String ROOM_DEST_URL = "/topic/rooms/";

    // 채팅방 발행
    public void publishRoomCreated(final CreateRoomEvent event) {
        messagingTemplate.convertAndSend(ROOM_DEST_URL + event.loginUserId(),event);
        messagingTemplate.convertAndSend(ROOM_DEST_URL + event.peerId(),event);
    }

    // 메시지 발행
    public void publishMessage(final MessageBroadcastResponse response){
        messagingTemplate.convertAndSend(MESSAGE_DEST_URL + response.roomId(),response);
    }

    // 읽은 메시지 표시 발행
    public void publishMessageRead(final MessageReadResponse response){
        messagingTemplate.convertAndSend(ROOM_DEST_URL + response.roomId(),response);
    }
}
