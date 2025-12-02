package org.chat.handler;

import lombok.RequiredArgsConstructor;
import org.chat.domain.service.ChatRoomUserRedisService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;


@Component
@RequiredArgsConstructor
public class StompEventListener {
    /**
     * 구독 이벤트를 가로채서 사용자들에게 알려줌
     */
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomUserRedisService redisService;
    private static final String USER_ID_KEY = "userId";
    private static final String USER_NICKNAME = "nickname";
    private final String ROOM_URL = "/topic/chat/room/";

    /**
     * 사용자가 방에 입퇴장했을 때
     */
    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        if (destination == null || !destination.startsWith(ROOM_URL)) {
            return;
        }
        String roomId = destination.substring(destination.lastIndexOf("/") + 1);

        String userId = (String) headerAccessor.getSessionAttributes().get(USER_ID_KEY);
        String sessionId = headerAccessor.getSessionId();
        if (userId != null) {
            redisService.addUser(roomId, userId, sessionId);
        }
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String userId = (String) headerAccessor.getSessionAttributes().get(USER_ID_KEY);
        String nickname = (String) headerAccessor.getSessionAttributes().get(USER_NICKNAME);
        String roomId = redisService.deleteUser(sessionId, userId);
         if (roomId != null) {
            messagingTemplate.convertAndSend(ROOM_URL + roomId, nickname + "님이 퇴장했습니다.");
         }
    }


}
