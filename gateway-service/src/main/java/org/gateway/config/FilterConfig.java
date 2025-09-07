package org.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder){
        return builder.routes()
                // 인증 서버
                .route(r -> r.path("/auth/**")
                        .filters(f -> f.rewritePath("/v1/api/(?<segment>.*)", "/${segment}"))
                        .uri("lb://auth-service"))
                // 채팅 서버
                .route(r -> r.path("/chat/**")
                        .filters(f -> f.rewritePath("/v1/api/(?<segment>.*)", "/${segment}"))
                        .uri("lb://chat-service"))
                // 실시간 사용자 서버
                .route(r -> r.path("/presence/**")
                        .filters(f -> f.rewritePath("/v1/api/(?<segment>.*)", "/${segment}"))
                        .uri("lb://presence-service"))
                .build();
    }
}
