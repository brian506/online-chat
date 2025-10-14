package org.chat.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.chat.domain.dto.Participants;
import org.chat.domain.dto.response.RoomResponse;
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
        @CompoundIndex(name = "uk_pair_emails", def = "{'p0_email':1, 'p1_email':1}", unique = true)
})
public class Room  {
    // todo 닉네임을 기준으로 복합인덱스 생성하여 조회 성능 향상

    @Id
    private String id;

    @Field(name = "room_type")
    private RoomType roomType;

    @Field(name = "participants")
    private List<Participants> participants; // 두명 asker,answerer

    @Setter
    @Field(name = "name")
    private String name; // 채팅방 이름 (별명으로)

    @Field(name = "created_at")
    private LocalDateTime createdAt;

    // ← 중복 방지 핵심: 정렬된 userId 페어로 생성한 키
    @Indexed(unique = true)
    @Field(name = "room_key")
    private String roomKey;

    public static Room of(Participants a, Participants b) {
        return Room.builder()
                .roomType(RoomType.PRIVATE)
                .participants(List.of(a, b))
                .createdAt(LocalDateTime.now())
                .roomKey(buildRoomKey(a.userId(), b.userId()))
                .build();
    }

    public static RoomResponse toDto(Room room){
        return new RoomResponse(
                room.id,
                room.getName(),
                room.getParticipants(),
                room.getRoomType(),
                room.getCreatedAt()
        );
    }
    /* roomKey = 사전순(userIdA, userIdB) 조합 */
    public static String buildRoomKey(String a, String b) {
        if(a.compareTo(b) <= 0) {
            return a + ":" + b;
        }
        return b + ":" + a;
    }

}
