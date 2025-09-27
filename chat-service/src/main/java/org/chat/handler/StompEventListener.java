package org.chat.handler;

import lombok.RequiredArgsConstructor;
import org.chat.domain.service.ChatRoomUserService;
import org.chat.security.StompPrincipal;
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
    private final ChatRoomUserService roomUserService;
    private final String ROOM_URL = "/topic/chat/room";

    // 사용자가 방에 입장했을 때
    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String roomId = accessor.getDestination().replace("/topic/chat/room/", "");
        String username = getUsernameFromAuthentication(accessor);
        String sessionId = accessor.getSessionId();

        roomUserService.addUser(roomId, username, sessionId);

        messagingTemplate.convertAndSend(ROOM_URL + roomId, username + "님이 입장하였습니다");
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = getUsernameFromAuthentication(accessor);
        String sessionId = accessor.getSessionId();
        String roomId = roomUserService.removeUser(username,sessionId);

        messagingTemplate.convertAndSend(ROOM_URL + roomId,
                username + "님이 퇴장했습니다.");
    }
    private String getUsernameFromAuthentication(StompHeaderAccessor accessor){
        StompPrincipal user = (StompPrincipal) accessor.getUser();
        return user.getUsername();
    }
}
