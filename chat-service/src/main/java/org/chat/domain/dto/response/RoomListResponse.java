package org.chat.domain.dto.response;

import java.util.List;

public record RoomListResponse(List<RoomResponse> roomResponses, String nextCursor) {
}
