package org.gateway.authentication;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * Global Filter
 * 클라이언트의 요청이 서비스에 전달되기 전에 실행( 로그인 성공 이후, API 접근할 때)
 * 인증/인가 처리 적합 -> 엑세스 헤더 추출하여 검증
 */
// todo 예외처리 생성
@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;
    private final String LOGIN_URL = "/oauth2/callback/google/login";

    public AuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    public static class Config {}

    // 로그인 -> jwt 반환 -> 사용자는 요청 시마다 jwt 헤더에 담아 전송
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // login 경로는 다음 필터로 넘어감
            String path = request.getURI().getPath();
            if (path.equals(LOGIN_URL)) {
                return chain.filter(exchange);
            }

            // 헤더 존재 여부 확인
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String token = authorizationHeader.replace("Bearer ", "");

            //  토큰 유효성 검증
            try{
                jwtUtil.validateToken(token);
            } catch (Exception e){
                throw new RuntimeException(e);
            }

            //  유효한 경우 다음 필터로 요청 전달
            return chain.filter(exchange);
        };
    }



    // 에러 발생 시 Mono<Void> 타입의 객체를 반환
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }
}
