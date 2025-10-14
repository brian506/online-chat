package org.chat.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.security.JwtUtil;
import org.chat.security.StompPrincipal;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99) // 시큐리티 필터보다 우선순위 낮게
public class StompHandler implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // stomp connect 명령어 일때만
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // stomp 헤더에서 jwt 추출 - 클라이언트가 메시지 전송 시 헤더에 토큰 받아서 보내야됨
            log.info("CONNECT 요청 수신. Authorization 헤더: {}", accessor.getFirstNativeHeader("Authorization"));
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("토큰이 없습니다.");
            }
            String token = authHeader.substring(7);

            Authentication authentication = jwtUtil.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            accessor.setUser(authentication);
            //  이후 프레임에서도 읽히도록
            accessor.setLeaveMutable(true);
            log.info("✅ STOMP CONNECT 인증 성공: user={}", authentication.getName());
        }
        return message;
    }
}

    // 사용자 이름을 세션에 저장하고 꺼내서 쓰는 메서드
    // CONNECT 시 accessor 에 저장해둔 authenticaiton 이 이후의 SUBSCRIBE,CONNECT 등 값이 파싱이 안돼서 세션에도 따로 저장해서 쓰도록함
//    private void createUserSession(Authentication authentication,StompHeaderAccessor accessor){
//        var principal = (StompPrincipal) authentication.getPrincipal();
//        accessor.getSessionAttributes().put("username", principal.getUsername());
//    }
//}
