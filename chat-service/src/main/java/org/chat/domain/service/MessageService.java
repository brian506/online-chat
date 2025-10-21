package org.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.request.SendMessageEvent;
import org.chat.domain.dto.response.MessageBroadcastResponse;
import org.chat.domain.entity.Message;
import org.chat.domain.repository.MessageRepository;
import org.chat.security.StompPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;


    public MessageBroadcastResponse createMessage(final SendMessageEvent message, String senderId){
        Message messageToSave = Message.saveMessage(message, senderId);
        Message savedMessage = messageRepository.save(messageToSave);
        log.debug("Message saved: roomId={}, sender={}, content={}",
                savedMessage.getRoomId(), savedMessage.getSenderId(), savedMessage.getContent());
        return Message.toDto(savedMessage);
    }
}
