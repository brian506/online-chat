package org.chat.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.security.JwtUtil;
import org.common.utils.UserPrincipal;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;


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
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            accessor.setUser(authentication);
            // 다른 프레임에서도 해당 인증 객체를 사용하기 위해 세션에 저장해둠
            Map<String, Object> attributes = accessor.getSessionAttributes();
            attributes.put("userId", principal.userId());
            attributes.put("nickname", principal.nickname());

            accessor.setLeaveMutable(true);
            log.info("✅ STOMP CONNECT 인증 성공: user={}", authentication.getName());
        }
        return message;
    }

}
