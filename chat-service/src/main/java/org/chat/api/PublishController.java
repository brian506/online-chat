package org.chat.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.request.CreateReadMessageEvent;
import org.chat.domain.dto.request.SendMessageRequest;
import org.chat.domain.service.MessageService;
import org.chat.domain.service.RoomService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PublishController {

    private final MessageService messageService;
    private final RoomService roomService;
    private static final String USER_ID_KEY = "userId";
    private static final String USER_NICKNAME = "nickname";

    //  "/pub/sendMessage" 경로로 클라이언트가 메시지 보냄
    @MessageMapping("/send/message") // 서버에서 메시지 수신
    public void sendMessage(@Payload SendMessageRequest event,
                            StompHeaderAccessor accessor){
        String senderId =(String) accessor.getSessionAttributes().get(USER_ID_KEY);
        String senderNickname =(String) accessor.getSessionAttributes().get(USER_NICKNAME);
        messageService.createMessage(event,senderId,senderNickname);
    }

    @MessageMapping("/mark/read-message")
    public void sendReadMessage(@Payload CreateReadMessageEvent event){
        roomService.updateToRead(event);
    }

    // 입장 알림 이벤트



}
/**
 * Http 가 아닌 STOMP 메시징인 @Authentication 의 StompPrincipal 을 읽지 못하므로 그냥 authentication(setUser) 에 등록된 Principal 을 가져다 씀
 */