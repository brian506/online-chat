package org.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${domain.websocket.chat}")
    private String chatUrl; // /game

    @Value("${domain.websocket.publish}")
    private String pub; // /pub

    @Value("${domain.websocket.subscribe}")
    private String sub; // /sub

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(sub);
        config.setApplicationDestinationPrefixes(pub); // client 가 서버로 메시지를 보낼 때 필수 경로 설정
     }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // todo 웹소켓에 접속허용할 주소 설정
        registry.addEndpoint(chatUrl).setAllowedOrigins("*")
                .withSockJS();
    }
}
