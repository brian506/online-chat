package org.chat.domain.dto.response;

public record RoomUserResponse(String roomId,
                               String peerLabel) {
    public static RoomUserResponse of(String roomId,String peerLabel){
        return new RoomUserResponse(roomId,peerLabel);
    }
}
// 요청자에게 보여줄 상대방 이름 or 별명
// 질문자에게는 상대방 이름
// 답변자에게는 상대방 별명(익명성 보장)