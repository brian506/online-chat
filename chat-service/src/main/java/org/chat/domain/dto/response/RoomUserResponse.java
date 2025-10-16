package org.chat.domain.dto.response;



/**
 * 방 생성(질문자)한 사람 관점의 DTO
 */
public record RoomUserResponse(String roomId,
                               String peerLabel) {
    public static RoomUserResponse of(String roomId,String peerLabel){
        return new RoomUserResponse(roomId,peerLabel);
    }
}

// 요청자에게 보여줄 상대방 이름 or 별명
// 질문자에게는 상대방 이름
// 답변자에게는 상대방 별명(익명성 보장)