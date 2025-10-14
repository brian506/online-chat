package org.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.entity.Message;
import org.chat.domain.repository.MessageRepository;
import org.chat.domain.repository.RoomRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final String DESTINATION_URL = "/topic/chat/room/";

    public void sendMessage(final Message message){
        Message messageToSave = Message.saveMessage(message);
        Message savedMessage = messageRepository.save(messageToSave);
        // 반환받은 객체를 사용하여 클라이언트에게 브로드캐스팅
        String destination = DESTINATION_URL + savedMessage.getRoomId();
        log.info("수신 destination : " + destination);
        messagingTemplate.convertAndSend(destination,savedMessage);
    }
}
