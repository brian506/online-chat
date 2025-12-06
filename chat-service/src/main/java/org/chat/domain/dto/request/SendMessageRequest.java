package org.chat.domain.dto.request;

/**
 * 메시지 웹소켓 발행용 dto
 */
public record SendMessageRequest(String roomId,String receiverId, String content, String clientMsgId) {
}
