package org.gateway.security;

import io.netty.handler.codec.http.HttpMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;


@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationFilter authenticationFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                // ★ CORS 모듈 활성화
                .cors(c -> c.configurationSource(corsSource()))
                .authorizeExchange(ex -> ex
                        // ★ 프리플라이트(OPTIONS) 전부 허용
                        .pathMatchers(String.valueOf(HttpMethod.OPTIONS), "/**").permitAll()
                        // 공개 경로
                        .pathMatchers("/v1/api/whisky/**").permitAll()
                        .pathMatchers("/ws/**").permitAll()
                        .pathMatchers("/chat-ws/**").permitAll()
                        .pathMatchers("/v3/api-docs/**").permitAll()
                        // 그 외는 인증
                        .anyExchange().authenticated()
                )
                .build();
    }
    @Bean
    public CorsConfigurationSource corsSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(Arrays.asList(
                "http://127.0.0.1:5500", "http://localhost:5500",
                "http://127.0.0.1:5173", "http://localhost:5173"
        ));
        c.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        c.setAllowedHeaders(Arrays.asList("Authorization","Content-Type","X-Requested-With"));
        c.setExposedHeaders(Arrays.asList("Authorization"));
        c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);
        return s;
    }

}
