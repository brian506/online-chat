package org.chat.domain.dto.request;

public record SendMessageRequest(String roomId, String content) {
}
