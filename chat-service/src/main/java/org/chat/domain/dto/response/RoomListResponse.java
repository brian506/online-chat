package org.chat.domain.dto.response;

import java.util.List;

/**
 * 다음 커서를 받기 위한 dto
 */
public record RoomListResponse(List<RoomResponse> roomResponses, String nextCursor) {
}
