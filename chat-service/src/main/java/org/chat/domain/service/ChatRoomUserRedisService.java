package org.chat.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatRoomUserRedisService {

    /**
     * 방에 참여자 목록 위한 레디스
     */

    private final StringRedisTemplate redisTemplate;
    private static final String ROOM_KEY_PREFIX = "chat:room:";
    private static final String SESSION_KEY_PREFIX = "chat:session:";

    private String getRoomKey(String roomId) {
        return ROOM_KEY_PREFIX + roomId + ":users";
    }

    private String getSessionKey(String sessionId) {
        return SESSION_KEY_PREFIX + sessionId;
    }

    // 사용자 입장
    public void addUser(String roomId, String userId, String sessionId) {
        redisTemplate.opsForSet().add(getRoomKey(roomId), userId);
        redisTemplate.opsForValue().set(getSessionKey(sessionId), roomId);
    }
    // 사용자 퇴장
    public String deleteUser(String sessionId, String userId) {
        String roomId = redisTemplate.opsForValue().get(getSessionKey(sessionId));
        redisTemplate.opsForSet().remove(getRoomKey(roomId), userId);
        redisTemplate.delete(getSessionKey(sessionId));
        return roomId;
    }

    // 사용자 있는 지 확인
    public boolean isUserConnected(String roomId, String userId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(getRoomKey(roomId), userId));
    }

    // 모든 사용자 조회
    public Set<String> getUsers(String roomId) {
        return redisTemplate.opsForSet().members(getRoomKey(roomId));
    }
}
