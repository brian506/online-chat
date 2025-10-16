package org.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.request.SendMessageRequest;
import org.chat.domain.dto.response.MessageBroadcastResponse;
import org.chat.domain.entity.Message;
import org.chat.domain.repository.MessageRepository;
import org.chat.domain.repository.RoomRepository;
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

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final String DESTINATION_URL = "/topic/chat/room/";

    // 채팅방 발행

    // 메시지 발행
    public void sendMessage(final SendMessageRequest message, Principal sender){
        // 먼저 수신 받은 메시지 저장
        Message messageToSave = Message.saveMessage(message, sender.getName());
        Message savedMessage = messageRepository.save(messageToSave);

        // 수신한 메시지를 다시 채팅방에 있는 사용자들에게 보내기 위해서 응답 객체로 감쌈
        MessageBroadcastResponse response = Message.toDto(savedMessage);
        // 반환받은 객체를 사용하여 클라이언트에게 브로드캐스팅
        String destination = DESTINATION_URL + response.roomId();
        log.info("수신 destination : " + destination);
        messagingTemplate.convertAndSend(destination,response);
    }
}
