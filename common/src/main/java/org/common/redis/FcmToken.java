package org.common.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "fcm_token")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FcmToken {

    @Id // UserId를 Key로 사용
    private String userId;

    private String token;
}
