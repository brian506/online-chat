package org.chat.config;

import lombok.RequiredArgsConstructor;
import org.chat.handler.StompHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;

    @Value("${domain.websocket.chat}")
    private String chatUrl; // /game

    @Value("${domain.websocket.publish}")
    private String pub; // /pub

    @Value("${domain.websocket.subscribe}")
    private String sub; // /sub

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(sub); // client 가 구독할 경로
        config.setApplicationDestinationPrefixes(pub); // client 가 서버로 메시지를 보낼 때 필수 경로 설정(서버가 메시지를 처리하는 경로)
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // todo 웹소켓에 접속허용할 주소 설정
        registry.addEndpoint(chatUrl)
                .setAllowedOriginPatterns("http://127.0.0.1:5500"); // cors 검사가 아닌 origin 핸드셰이크 허
        // sockJS() 안쓰므로 네이티브 웹소켓임

    }

    // stomp 메시지 처리 전, 인증을 위한 인터셉터 등록
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}
