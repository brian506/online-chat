package org.chat.api;

import lombok.RequiredArgsConstructor;
import org.chat.domain.entity.Message;
import org.chat.domain.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(Message message){
        chatService
    }
}
