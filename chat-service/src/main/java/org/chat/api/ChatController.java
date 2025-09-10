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

    @MessageMapping("/sendMessage") // 서버에서 메시지 수신
    public void sendMessage(Message message){
        chatService.sendMessage(message);
    }
}
