package org.auth.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.common.utils.UserPrincipal;
import org.auth.domain.entity.Role;
import org.auth.domain.entity.Token;
import org.auth.domain.entity.AuthUser;
import org.auth.domain.repository.AuthUserRepository;
import org.auth.domain.repository.redis.TokenRepository;
import org.auth.domain.dto.response.AccessTokenPayload;
import org.auth.security.service.CookieService;
import org.auth.security.service.JwtService;
import org.common.utils.OptionalUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String NO_CHECK_URL = "/v1/api/auth/login";

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final CookieService cookieService;
    private final AuthUserRepository authUserRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("Incoming request URI: {}", request.getRequestURI());

        if (request.getRequestURI().startsWith(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }
        Optional<String> accessTokenOpt = jwtService.extractAccessToken(request);

        if (accessTokenOpt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = accessTokenOpt.get();

        try {
            setAuthentication(accessToken);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.info("AccessToken 이 만료되었습니다. RefreshToken 재발급 시도");
            try {
                refreshTokensAndContinue(request, response, filterChain);
            } catch (JwtException | IOException | ServletException refreshException) {
                log.warn("RefreshToken 재발급 실패: {}", refreshException.getMessage());
                handleAuthError(response, "로그인 재시도 필요");
            }
        }
    }


    // 리프레시 토큰을 이용한 새로운 엑세스 토큰 재발급
    private void refreshTokensAndContinue(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String refreshToken = cookieService.getRefreshTokenFromCookie(request)
                .orElseThrow(() -> new JwtException("존재하지 않는 cookie"));
        jwtService.verifyToken(refreshToken);

        Token token = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new JwtException("Redis에 존재하지 않거나 만료된 Refresh Token 입니다."));
        AuthUser authUser = OptionalUtil.getOrElseThrow(authUserRepository.findById(token.getUserId()),"존재하지 않는 사용자입니다.");

        String newAccessToken = jwtService.createAccessToken(
                new AccessTokenPayload(token.getUserId(), authUser.getNickname(), Role.GENERAL, new Date())
        );

        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);
        setAuthentication(newAccessToken);
        filterChain.doFilter(request, response);
    }


    private void setAuthentication(String accessToken) {
        Claims claims = jwtService.verifyToken(accessToken);
        String userId = claims.getSubject();
        String nickname = claims.get("nickname",String.class);
        String role = claims.get("role", String.class);
        UserPrincipal userPrincipal = UserPrincipal.of(userId,nickname);
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, List.of(grantedAuthority));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void handleAuthError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
    }
}
