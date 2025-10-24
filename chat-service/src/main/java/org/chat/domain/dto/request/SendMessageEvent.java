package org.chat.domain.dto.request;

/**
 * 메시지 발행용 dto
 */
public record SendMessageEvent(String roomId, String content,String clientMsgId) {
}
