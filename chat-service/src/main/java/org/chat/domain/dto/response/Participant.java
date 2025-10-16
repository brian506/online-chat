package org.chat.domain.dto.response;

import org.chat.domain.entity.UserType;

public record Participant(String userId, String nickname, String username,UserType userType) {

    // 질문자용
    public static Participant asker(String userId, String nickname,String username ) {
        return new Participant(userId, nickname, username, UserType.ASKER);
    }

    // 답변자용
    public static Participant answerer(String userId, String nickname,String username) {
        return new Participant(userId, nickname, username,UserType.ANSWERER);
    }
}
