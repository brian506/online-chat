package org.chat.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.chat.domain.dto.request.CreateReadMessageEvent;
import org.chat.domain.dto.response.AnswerFromBoardResponse;
import org.chat.domain.dto.response.MessageReadResponse;
import org.chat.domain.dto.response.RoomResponse;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Document(collection = "rooms")
@CompoundIndexes({
        @CompoundIndex(
                name = "idx_part_user_type_last",
                def = "{'participants.user_id' : 1, 'participants.user_type' : 1 , 'last_message_at' : -1}"
        )
}) // 질문 받은, 질문한 목록들을 (사용자ID + UserType)복합인덱스를 사용하여 최근 채팅순으로 조회 최적화
public class Room  {
    // todo 닉네임을 기준으로 복합인덱스 생성하여 조회 성능 향상

    @Id
    private String id; // 답변Id 로?

    @Field(name = "room_type")
    private RoomType roomType;

    @Field(name = "participants") // 질문자,답변자 등 정보(실명,닉네임)
    private List<Participant> participants;

    @Field(name = "created_at")
    private LocalDateTime createdAt;


    public static Room ofPrivateRoom(AnswerFromBoardResponse response,Participant asker, Participant answerer) {
        return Room.builder()
                .id(response.answerId())
                .roomType(RoomType.PRIVATE)
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



}
