package org.gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
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
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // CSRF 비활성화
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // Form 로그인 비활성화
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // HTTP Basic 인증 비활성화
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/oauth2/**").permitAll()
                        .pathMatchers("/chat/**").permitAll()
                        .anyExchange().authenticated()
                )
                .build();
    }

}
