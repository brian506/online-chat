package org.board.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.common.exception.custom.JwtValidationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class BoardTokenVerificationFilter  extends OncePerRequestFilter {

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
            SecurityContextHolder.clearContext();
            throw new JwtValidationException("유효하지 않은 JWT 토큰입니다.");
        }

    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/v3/api-docs/**");
    }

    private void setAuthentication(String accessToken) {
        Authentication authentication = jwtUtils.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

