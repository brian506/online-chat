package org.chat.domain.dto.response;

import org.chat.domain.entity.Participant;
import org.chat.domain.entity.RoomType;

import java.time.LocalDateTime;
import java.util.List;

public record RoomResponse(String roomId, String roomName, List<Participant> participants, RoomType roomType, LocalDateTime createdAt, LocalDateTime lastMessageAt) {
}
