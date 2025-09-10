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

    public void sendMessage(Message message){
       Message messageToSave = Message.saveMessage(message);

       messageRepository.save(messageToSave)
               .doOnSuccess(savedMessage -> {
                   // 브로드캐스팅 ( 클라이언트들이 메시지를 받기 위해 구독하는 주소 )
                   String destination = "/sub/chat/room/" + savedMessage.getRoomId();
                   messagingTemplate.convertAndSend(destination,savedMessage);
               })
               .subscribe();
    }
}
