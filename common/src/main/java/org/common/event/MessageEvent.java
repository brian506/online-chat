package org.common.event;

import java.time.LocalDateTime;

// 카프카 발행용 dto
public record MessageEvent(
        String roomId,
        String receiverId,
        String content,
        String senderId,
        String senderNickname,
        LocalDateTime timeStamp
) {
}
