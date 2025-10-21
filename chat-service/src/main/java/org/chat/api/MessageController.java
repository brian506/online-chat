package org.chat.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.request.SendMessageEvent;
import org.chat.domain.service.PublishService;
import org.chat.security.StompPrincipal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageController {

    private final PublishService publishService;
    private static final String USER_ID_KEY = "stompUserId";

    //  "/pub/sendMessage" 경로로 클라이언트가 메시지 보냄
    @MessageMapping("/sendMessage") // 서버에서 메시지 수신
    public void sendMessage(@Payload SendMessageEvent event,
                            StompHeaderAccessor accessor){
        String userId =(String) accessor.getSessionAttributes().get(USER_ID_KEY);
        publishService.sendMessage(event,userId);
    }

    // 입장 알림 이벤트
}
/**
 * Http 가 아닌 STOMP 메시징인 @Authentication 의 StompPrincipal 을 읽지 못하므로 그냥 authentication(setUser) 에 등록된 Principal 을 가져다 씀
 */