package org.chat.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * 토큰의 유효성 검증과, Claim 을 추출하여 사용자의 정보를 토대로 인가작업까지 수행한다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenVerificationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 헤더에서 AccessToken 추출
        Optional<String> accessTokenOpt = jwtUtils.extractAccessToken(request);
        if (accessTokenOpt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        // 2. AccessToken 검증 및 인증 정보 설정
        String accessToken = accessTokenOpt.get();
        try {
            setAuthentication(accessToken);
            filterChain.doFilter(request,response);
        } catch (Exception e) {
            log.warn("유효하지 않은 JWT 토큰입니다. URI: {}, Error: {}", request.getRequestURI(), e.getMessage());
            SecurityContextHolder.clearContext();
        }

    }


    private void setAuthentication(String accessToken) {
        Claims claims = jwtUtils.parseClaims(accessToken);
        String email = claims.getSubject();
        String role = claims.get("role", String.class);
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, List.of(grantedAuthority));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

