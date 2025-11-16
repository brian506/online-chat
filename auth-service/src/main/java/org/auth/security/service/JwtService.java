package org.auth.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.auth.domain.dto.response.AccessTokenPayload;
import org.auth.domain.dto.response.RefreshTokenPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class JwtService {

    private final SecretKey secretKey;

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${jwt.access.expiration}")
    private long accessKeyExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshKeyExpiration;


    public JwtService(@Value("${jwt.secretKey}") String secretKey) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // 커스텀 예외 추가
    public Claims verifyToken(final String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String createAccessToken(final AccessTokenPayload payload) {
        return Jwts.builder()
                .setSubject(payload.userId())
                .claim("role", payload.role().getKey())
                .claim("email",payload.email())
                .setIssuer(issuer)
                .setIssuedAt(payload.date())
                .setExpiration(new Date(payload.date().getTime() + accessKeyExpiration * 1000L))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public String createRefreshToken(final RefreshTokenPayload payload) {
        return Jwts.builder()
                .setSubject(payload.userId())
                .setIssuer(issuer)
                .setIssuedAt(payload.date())
                .setExpiration(new Date(payload.date().getTime() + refreshKeyExpiration + 1000L))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();

    }


    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(accessToken -> accessToken.startsWith("Bearer "))
                .map(accessToken -> accessToken.replace("Bearer ", ""));
    }

}
