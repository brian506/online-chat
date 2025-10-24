package org.chat.domain.dto.response;

import java.time.LocalDateTime;

public record MessageBroadcastResponse(
        String id,
        String roomId,
        String content,
        String senderId,
        String clientMsgId,
        LocalDateTime timeStamp
) {
}
