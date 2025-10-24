package org.chat.domain.dto.response;

import java.time.LocalDateTime;

public record MessageResponse(String id, String roomId, String content, String senderId, LocalDateTime timestamp) {
}
