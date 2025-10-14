package org.chat.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatRoomUserService {

    /**
     * 방에 참여자 목록 위한 레디스
     * 서버가 여러개 일때를 고려하여 sessionId 도 레디스에서 관리
     * 추후에 단체방에서 사용
     */

    private final StringRedisTemplate redisTemplate;
    private static final String ROOM_KEY_PREFIX = "chat:room:";
    private static final String SESSION_KEY_PREFIX = "chat:session:";

    private String getRoomKey(String roomId){
        return ROOM_KEY_PREFIX + roomId + ":users";
    }
    private String getSessionKey(String sessionId) {
        return SESSION_KEY_PREFIX + sessionId;
    }

    // 사용자 추가
    public void addUser(String roomId, String nickname,String sessionId){
        redisTemplate.opsForSet().add(getRoomKey(roomId),nickname);
        redisTemplate.opsForValue().set(getSessionKey(sessionId), roomId);
    }
    // 사용자 제거(세션에서 roomId 찾아서 제거)
    public String removeUser(String nickname,String sessionId){
        String roomId = redisTemplate.opsForValue().get(getSessionKey(sessionId));
        if (roomId != null) {
            redisTemplate.opsForSet().remove(getRoomKey(roomId), nickname);
            redisTemplate.delete(getSessionKey(sessionId));
        }
        return roomId;
    }
    // 방에 있는 모든 사용자 조회
    public Set<String> getUsers(String roomId){
        return redisTemplate.opsForSet().members(getRoomKey(roomId));
    }
    // 방 비우기
    public void clearRoom(String roomId,String sessionId){
        redisTemplate.delete(getRoomKey(roomId));
        redisTemplate.delete(getSessionKey(sessionId));
    }

}
