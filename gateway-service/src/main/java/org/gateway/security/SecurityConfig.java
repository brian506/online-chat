package org.gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationFilter authenticationFilter;
    private final CorsConfig corsConfig;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .cors(cors -> cors.configurationSource((CorsConfigurationSource) corsConfig.corsConfigurationSource())) // CORS 설정
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // CSRF 비활성화
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // Form 로그인 비활성화
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // HTTP Basic 인증 비활성화
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/oauth2/**").permitAll()
                        .anyExchange().authenticated()
                )
                .build();
    }
}
