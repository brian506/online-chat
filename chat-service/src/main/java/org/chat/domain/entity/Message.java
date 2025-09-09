package org.chat.domain.entity;


import lombok.Builder;
import lombok.Getter;
import org.chat.domain.dto.Sender;
import org.common.config.BaseTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Builder
@Document(collection = "messages")
//todo 복합 인덱스 등 추후에 세팅
public class Message  {

    @Id
    private String id;

    @Field(name = "room_id")
    private Long roomId;

    @Field(name = "content")
    private String content;

    @Field(name = "sender")
    private Sender sender;

    @Field(name = "timestamp")
    private LocalDateTime timestamp;

    public static Message saveMessage(Message message){
       return Message.builder()
                .roomId(message.getRoomId())
                .content(message.getContent())
                .sender(message.getSender())
                .timestamp(LocalDateTime.now())
                .build();
    }
}

