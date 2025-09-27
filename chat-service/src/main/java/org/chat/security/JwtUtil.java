
package org.chat.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.common.exception.custom.JwtValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secretKey}")
    private String secretKey;

    public Claims parseClaims(final String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtValidationException("JWT 토큰 검증 실패");
        }
    }

    // 토큰을 바탕으로 검증과 Authentication 객체 반환
    public Authentication getAuthentication(String accessToken) {
        // 여기서 jwt 토큰 검증
        Claims claims = parseClaims(accessToken);
        String email = claims.getSubject();
        String role = claims.get("role", String.class);
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
        return new UsernamePasswordAuthenticationToken(email,null,List.of(grantedAuthority));
    }
    // 토큰 안에 있는 사용자 이름만 반환하기 위한 메소드
    public String getUsernameFromToken(final String accessToken) {
        Claims claims = parseClaims(accessToken);
        return claims.get("username",String.class);
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(accessToken -> accessToken.startsWith("Bearer "))
                .map(accessToken -> accessToken.replace("Bearer ", ""));
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
