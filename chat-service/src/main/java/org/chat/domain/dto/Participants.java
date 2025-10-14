package org.chat.domain.dto;

import org.chat.domain.entity.UserType;

public record Participants(String userId, String nickname, String realName,UserType userType) {

    // 질문자용
    public static Participants asker(String userId, String nickname,String realName ) {
        return new Participants(userId, nickname, realName, UserType.ASKER);
    }

    // 답변자용
    public static Participants answerer(String userId, String nickname,String realName) {
        return new Participants(userId, nickname, realName,UserType.ANSWERER);
    }
}
