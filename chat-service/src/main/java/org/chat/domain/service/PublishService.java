package org.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.request.CreateRoomEvent;
import org.chat.domain.dto.request.SendMessageEvent;
import org.chat.domain.dto.response.MessageBroadcastResponse;
import org.chat.domain.entity.Message;
import org.chat.domain.repository.MessageRepository;
import org.chat.security.StompPrincipal;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PublishService {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final String MESSAGE_DEST_URL = "/topic/chat/room/";
    private final String ROOM_DEST_URL = "/topic/rooms/";

    // 채팅방 발행
    public void publishRoomCreated(final CreateRoomEvent event) {
        messagingTemplate.convertAndSend(ROOM_DEST_URL + event.askerId(),event);
        messagingTemplate.convertAndSend(ROOM_DEST_URL + event.answererId(),event);
    }

    // 메시지 발행
    public void sendMessage(final SendMessageEvent event,String senderId){
        MessageBroadcastResponse response = messageService.createMessage(event,senderId);
        messagingTemplate.convertAndSend(MESSAGE_DEST_URL + response.roomId(),response);
    }

}
