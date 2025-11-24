package org.chat.domain.dto.response;



/**
 * 방 생성한 사람 관점의 DTO
 */
public record RoomUserResponse(String roomId,
                               String peerNickname) {
    public static RoomUserResponse of(String roomId,String peerNickname){
        return new RoomUserResponse(roomId,peerNickname);
    }
}

// 요청자에게 보여줄 상대방 이름 or 별명
