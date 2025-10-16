package org.chat.domain.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public record Participant(@Field("user_id") String userId,
                          @Field("nickname") String nickname,
                          @Field("username") String username,
                          @Field("user_type") UserType userType
) {
public static Participant asker(String userId, String nickname, String username) {
    return new Participant(userId, nickname, username, UserType.ASKER);
}

public static Participant answerer(String userId, String nickname, String username) {
    return new Participant(userId, nickname, username, UserType.ANSWERER);
}
}
