package org.chat.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.chat.domain.dto.Participants;
import org.chat.domain.entity.Room;
import org.chat.domain.entity.RoomType;

import java.time.LocalDateTime;
import java.util.List;

public record RoomResponse(String roomId, String roomName, List<Participants> participants, RoomType roomType,LocalDateTime createdAt) {
}
