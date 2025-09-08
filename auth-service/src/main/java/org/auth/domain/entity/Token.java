package org.auth.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refresh_token",timeToLive = 604800) // 7일
@AllArgsConstructor
@Getter
@ToString
public class Token  {

    @Id
    private String email;

    private String refreshToken;

    private Role role;

}
