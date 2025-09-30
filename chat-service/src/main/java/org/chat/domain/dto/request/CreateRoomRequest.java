package org.chat.domain.dto.request;

import org.chat.domain.dto.Participants;

import java.util.List;

public record CreateRoomRequest(String id, String name, List<Participants> participants) {
}
