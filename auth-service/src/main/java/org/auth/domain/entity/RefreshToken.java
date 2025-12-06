package org.auth.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;


@RedisHash(value = "refresh_token",timeToLive = 604800) // 7일
@AllArgsConstructor
@Getter
@ToString
@Builder
public class RefreshToken {

    @Id
    private String refreshToken;

    @Indexed
    private String userId;

    private Role role;

    private LocalDateTime createdAt;

    public static RefreshToken toEntity(AuthUser user, String token){
        return RefreshToken.builder()
                .refreshToken(token)
                .userId(user.getId())
                .role(user.getRole())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
