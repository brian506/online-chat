package org.chat.domain.dto.request;

public record CreateReadMessageEvent(String roomId,String userId,String messageId) {
}
