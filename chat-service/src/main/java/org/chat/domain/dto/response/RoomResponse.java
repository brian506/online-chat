package org.chat.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chat.domain.dto.Participants;
import org.chat.domain.entity.Room;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class RoomResponse {
    private String id;
    private String name;
    private List<Participants> participants;
    private LocalDateTime createdAt;

    public static RoomResponse toDto(Room room){
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .participants(room.getParticipants())
                .createdAt(room.getCreatedAt())
                .build();
    }
}
