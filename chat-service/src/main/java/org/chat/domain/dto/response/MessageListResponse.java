package org.chat.domain.dto.response;

import java.util.List;

public record MessageListResponse(List<MessageResponse> messages, String nextCursor) {
}
