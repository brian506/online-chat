//package org.chat.handler;
//
//import lombok.RequiredArgsConstructor;
//import org.chat.domain.entity.ChatUser;
//import org.chat.domain.repository.ChatUserRepository;
//import org.chat.domain.service.ChatRoomUserService;
//import org.chat.security.StompPrincipal;
//import org.common.utils.OptionalUtil;
//import org.springframework.context.event.EventListener;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//import org.springframework.web.socket.messaging.SessionSubscribeEvent;
//
//import java.security.Principal;
//import java.util.Optional;
//
//@Component
//@RequiredArgsConstructor
//public class StompEventListener {
//    /**
//     * 구독 이벤트를 가로채서 사용자들에게 알려줌
//     */
//    private final SimpMessagingTemplate messagingTemplate;
//    private final ChatUserRepository userRepository;
//    private final String ROOM_URL = "/topic/chat/room";
//
//    /**
//     * 사용자가 방에 입퇴장했을 때(별명으로 브로드캐스팅)
//     */
//
//
//    @EventListener
//    public void handleSubscribeEvent(SessionSubscribeEvent event) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
//        String roomId = accessor.getDestination().replace(ROOM_URL, "");
//        StompPrincipal principal =(StompPrincipal) accessor.getUser();
//        ChatUser user = OptionalUtil.getOrElseThrow(userRepository.findByEmail(principal.getName()),"존재하지 않는 사용자입니다.");
//        messagingTemplate.convertAndSend(ROOM_URL + roomId, user.getNickname() + "님이 입장하였습니다");
//    }
//
//    @EventListener
//    public void handleDisconnectEvent(SessionDisconnectEvent event) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
//        String roomId = accessor.getDestination().replace(ROOM_URL, "");
//        StompPrincipal principal =(StompPrincipal) accessor.getUser();
//        ChatUser user = OptionalUtil.getOrElseThrow(userRepository.findByEmail(principal.getName()),"존재하지 않는 사용자입니다.");
//        messagingTemplate.convertAndSend(ROOM_URL + roomId,
//                user.getNickname() + "님이 퇴장했습니다.");
//    }
//
//}
