package org.chat.domain.dto.request;

/**
 * 채팅방 생성 발행용 dto - 메시징 큐 이용시 사용
 */
public record CreateRoomEvent(String roomId,String askerId,String answererId) {
    public static CreateRoomEvent of(String roomId,String askerId, String answererId){
        return new CreateRoomEvent(roomId,askerId,answererId);
    }

}

