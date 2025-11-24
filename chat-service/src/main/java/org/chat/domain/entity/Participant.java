package org.chat.domain.entity;

import org.chat.domain.dto.response.ChatUserResponse;
import org.common.utils.UserPrincipal;
import org.springframework.data.mongodb.core.mapping.Field;

public record Participant(@Field("user_id") String userId,
                          @Field("nickname") String nickname,
                          @Field("last_read_message_id") String lastReadMessageId
) {
public static Participant peer(ChatUserResponse response) {
    return new Participant(response.userId(), response.nickname(), null);
}

public static Participant me(UserPrincipal me) {
    return new Participant(me.userId(), me.nickname(), null);
}


public Participant withLastMessageId(String lastReadMessageId){
    return new Participant(this.userId,this.nickname,lastReadMessageId);
}
}
