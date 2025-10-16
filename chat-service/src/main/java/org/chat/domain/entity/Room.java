package org.chat.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.chat.domain.dto.response.Participant;
import org.chat.domain.dto.response.RoomResponse;
import org.common.exception.custom.DataNotFoundException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
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
                def = "{'participants.userId' : 1, 'participants.type' : 1 , 'last_message_at' : -1}"
        )
}) // 질문 받은, 질문한 목록들을 (사용자ID + UserType)복합인덱스를 사용하여 최근 채팅순으로 조회 최적화
public class Room  {
    // todo 닉네임을 기준으로 복합인덱스 생성하여 조회 성능 향상

    @Id
    private String id;

    @Field(name = "room_type")
    private RoomType roomType;

    @Field(name = "participants") // 질문자,답변자 등 정보(실명,닉네임)
    private List<Participant> participants;

    @Setter
    @Field(name = "name")
    private String name; // 채팅방 이름 (별명으로)

    @Indexed(unique = true)
    @Field(name = "room_key")
    private String roomKey; // 중복 생성 방지

    @Field(name = "created_at")
    private LocalDateTime createdAt;

    @Field(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    public static Room ofPrivateRoom(Participant asker, Participant answerer) {
        return Room.builder()
                .roomType(RoomType.PRIVATE)
                .participants(List.of(asker, answerer))
                .roomKey(directionalKey(asker.userId(),answerer.userId()))
                .createdAt(LocalDateTime.now())
                .lastMessageAt(LocalDateTime.now())
                .build();
    }

    public static RoomResponse toDto(Room room){
        return new RoomResponse(
                room.id,
                room.getName(),
                room.getParticipants(),
                room.getRoomType(),
                room.getCreatedAt(),
                room.getLastMessageAt()
        );
    }

    // 질문자 -> 답변자 순서 고정 생성
    public static String directionalKey(String askerId,String answererId) {
        if (answererId == null || askerId == null) throw new DataNotFoundException("질문자 혹은 답변자가 존재하지 않습니다.");
        return askerId + "->" + answererId; // 방향성 유지!
    }

}
