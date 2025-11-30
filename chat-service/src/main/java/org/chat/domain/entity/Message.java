package org.chat.domain.entity;


import lombok.Builder;
import lombok.Getter;
import org.common.event.SendMessageEvent;
import org.chat.domain.dto.response.MessageBroadcastResponse;
import org.chat.domain.dto.response.MessageResponse;
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
    private String roomId;

    @Field(name = "content")
    private String content;

    @Field(name = "sender_id")
    private String senderId;

    @Field(name = "timestamp")
    private LocalDateTime timestamp;

    public static Message saveMessage(SendMessageEvent payload, String senderId){
       return Message.builder()
               .roomId(payload.roomId())
               .senderId(senderId)
               .content(payload.content())
               .timestamp(LocalDateTime.now())
                .build();
    }

    public static MessageBroadcastResponse toBroadCastResponse(Message message, String clientMsgId){
        return new MessageBroadcastResponse(
                message.getId(),
                message.getRoomId(),
                message.getContent(),
                message.getSenderId(),
                clientMsgId,
                message.getTimestamp()
        );
    }

    public static MessageResponse toRoomResponse(Message message){
        return new MessageResponse(
                message.getId(),
                message.getRoomId(),
                message.getContent(),
                message.getSenderId(),
                message.getTimestamp()
        );
    }

}

