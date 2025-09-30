package org.chat.api;

import lombok.RequiredArgsConstructor;
import org.chat.domain.entity.Message;
import org.chat.domain.service.ChatService;
import org.chat.domain.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    //  "/pub/sendMessage" 경로로 클라이언트가 메시지 보냄
    @MessageMapping("/sendMessage") // 서버에서 메시지 수신
    public void sendMessage(Message message){
        messageService.sendMessage(message);
    }

}
