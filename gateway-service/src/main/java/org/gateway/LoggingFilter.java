package org.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 요청이 들어왔을 때 로그를 기록합니다.
        log.info("======> Gateway received request. URI: {}", exchange.getRequest().getURI());

        // 다음 필터로 요청을 전달합니다.
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 필터의 실행 순서를 가장 높게 설정하여 제일 먼저 실행되도록 합니다.
        return -1;
    }
}
