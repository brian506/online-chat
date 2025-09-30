package org.gateway.security;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

import org.common.exception.custom.JwtValidationException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * Global Filter
 * 클라이언트의 요청이 서비스에 전달되기 전에 실행( 로그인 성공 이후, API 접근할 때)
 * 인증/인가 처리 적합 -> 엑세스 헤더 추출하여 검증
 * 사용자의 Claim 은 추출하지 않고 토큰의 유효성만 검증한다.
 *  SecurityWebFilterChain의 검사를 통과한 후, 실제 마이크로서비스로 라우팅되기 직전에 실행
 */
// todo 예외처리 생성
@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;
    private final String LOGIN_URL = "/oauth2/callback/google/login";
    private final String WEBSOCKET_URL = "/chat";

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
            if (path.startsWith(LOGIN_URL)) {
                log.info(">>> Gateway AuthenticationFilter 로그인 요청 : {}", path);
                return chain.filter(exchange);
            }

            // 헤더 존재 여부 확인
            String token = resolveToken(request);
            if (token == null) {
                return onError(exchange, "Token is missing", HttpStatus.UNAUTHORIZED);
            }

            // 웹소켓 연결은 인증 헤더만 체크 후  다음 필터로 넘어감
            if(path.startsWith(WEBSOCKET_URL)){
                log.info(">>> Gateway AuthenticationFilter 웹소켓 연결: {}", path);
                return chain.filter(exchange);
            }

            //  토큰 유효성 검증
            try{
                jwtUtil.validateToken(token);
                log.info("토큰 유효성 검증 : " + token);
            } catch (ExpiredJwtException e){
                throw new JwtValidationException("유효하지 않은 토큰입니다");
            }

            //  유효한 경우 다음 필터로 요청 전달
            return chain.filter(exchange);
        };
    }
    private String resolveToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }



    // 에러 발생 시 Mono<Void> 타입의 객체를 반환
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }
}
