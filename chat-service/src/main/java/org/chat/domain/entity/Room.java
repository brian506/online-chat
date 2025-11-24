package org.chat.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.chat.domain.dto.request.CreateReadMessageEvent;
import org.chat.domain.dto.response.MessageReadResponse;
import org.chat.domain.dto.response.RoomResponse;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Document(collection = "rooms")
public class Room  {
    // todo 닉네임을 기준으로 복합인덱스 생성하여 조회 성능 향상

    @Id
    private String id;

    @Field(name = "room_type")
    private RoomType roomType;

    @Indexed(unique = true)
    @Field(name = "room_key")
    private String roomKey;

    @Field(name = "participants") // 질문자,답변자 등 정보(실명,닉네임)
    private List<Participant> participants;

    @Field(name = "created_at")
    private LocalDateTime createdAt;


    public static Room ofPrivateRoom(Participant asker, Participant answerer,String roomKey) {
        return Room.builder()
                .roomType(RoomType.PRIVATE)
                .roomKey(roomKey)
                .participants(List.of(asker, answerer))
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static RoomResponse toRoomResponse(Room room, String peerName){
        return new RoomResponse(
                room.getId(),
                room.getParticipants(),
                room.getRoomType(),
                room.getCreatedAt(),
                peerName // 채팅방에서 보여질 상대방의 이름 or 닉네임
        );
    }

    public static MessageReadResponse toReadResponse(CreateReadMessageEvent event){
        return new MessageReadResponse(
                event.roomId(),
                event.userId(),
                event.messageId()
        );
    }

    // 중복키 생성 메서드
    public static String generateRoomKey(String userId1, String userId2) {
        if (userId1.compareTo(userId2) < 0) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

}
