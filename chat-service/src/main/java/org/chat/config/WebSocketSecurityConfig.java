package org.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
public class WebSocketSecurityConfig  {
    /**
     * STOMP 메시징에 대한 보안 규정
     */
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager() {
        // ★ 빈 주입 대신 직접 생성
        MessageMatcherDelegatingAuthorizationManager.Builder messages =
                MessageMatcherDelegatingAuthorizationManager.builder();

        return messages
                .nullDestMatcher().authenticated()                 // CONNECT 등 목적지 없는 프레임
                .simpDestMatchers("/pub/**").authenticated()       // 클라 → 서버 전송
                .simpSubscribeDestMatchers("/topic/**").authenticated() // 구독
                .simpTypeMatchers(
                        SimpMessageType.CONNECT,
                        SimpMessageType.DISCONNECT,
                        SimpMessageType.HEARTBEAT,
                        SimpMessageType.OTHER
                ).permitAll()                                      // 기술 프레임 허용
                .anyMessage().denyAll()
                .build();
    }
}
