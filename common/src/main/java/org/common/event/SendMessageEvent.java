package org.common.event;

/**
 * 메시지 발행용 dto
 */
public record SendMessageEvent(String roomId, String content,String clientMsgId) {
}
