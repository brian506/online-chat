package org.chat.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.request.SendMessageRequest;
import org.chat.domain.service.PublishService;
import org.chat.security.StompPrincipal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final PublishService messageService;

    //  "/pub/sendMessage" 경로로 클라이언트가 메시지 보냄
    @MessageMapping("/sendMessage") // 서버에서 메시지 수신
    public void sendMessage(@Payload SendMessageRequest message,
                            Principal sender){
        log.info("WS SEND /pub/sendMessage roomId={}, content={}, principal={}",
                message.roomId(), message.content(),
                sender != null ? sender.getName() : "null");
        messageService.sendMessage(message,sender);
    }

}
/**
 * Http 가 아닌 STOMP 메시징인 @Authentication 의 StompPrincipal 을 읽지 못하므로 그냥 authentication(setUser) 에 등록된 Principal 을 가져다 씀
 */