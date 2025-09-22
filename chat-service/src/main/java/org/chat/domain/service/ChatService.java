package org.chat.domain.service;

import lombok.RequiredArgsConstructor;
import org.chat.domain.entity.Message;
import org.chat.domain.repository.MessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessage(final Message message){
        // 1. 메시지 객체 생성
       Message messageToSave = Message.saveMessage(message);

       // 2. 메시지 저장하고 클라이언트들에게 브로드캐스팅
       messageRepository.save(messageToSave)
               .doOnSuccess(savedMessage -> {
                   // 브로드캐스팅 ( 클라이언트들이 메시지를 받기 위해 구독하는 주소 )
                   String destination = "/topic/chat/room/" + savedMessage.getRoomId();
                   messagingTemplate.convertAndSend(destination,savedMessage);
               })
               .subscribe();
    }
}
