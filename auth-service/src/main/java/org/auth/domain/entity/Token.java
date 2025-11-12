package org.auth.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.UUID;

@RedisHash(value = "refresh_token",timeToLive = 604800) // 7일
@AllArgsConstructor
@Getter
@ToString
@Builder
public class Token  {

    @Id
    private UUID userId;

    private String refreshToken;

    private Role role;

    private LocalDateTime createdAt;

    public static Token toEntity(AuthUser user, String token){
        return Token.builder()
                .userId(user.getId())
                .refreshToken(token)
                .role(user.getRole())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
