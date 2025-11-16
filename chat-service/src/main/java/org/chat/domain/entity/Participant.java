package org.chat.domain.entity;

import org.chat.domain.dto.response.AnswerFromBoardResponse;
import org.springframework.data.mongodb.core.mapping.Field;

public record Participant(@Field("user_id") String userId,
                          @Field("nickname") String nickname,
                          @Field("user_type") UserType userType,
                          @Field("last_read_message_id") String lastReadMessageId
) {
public static Participant asker(String userId, String nickname) {
    return new Participant(userId, nickname,  UserType.ASKER,null);
}

public static Participant answerer(String userId, String nickname) {
    return new Participant(userId, nickname, UserType.ANSWERER,null);
}


public Participant withLastMessageId(String lastReadMessageId){
    return new Participant(this.userId,this.nickname,this.userType,lastReadMessageId);
}
}
