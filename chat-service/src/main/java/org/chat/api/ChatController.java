package org.chat.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.request.CreateRoomRequest;
import org.chat.domain.dto.response.RoomResponse;
import org.chat.domain.dto.response.RoomUserResponse;
import org.chat.domain.entity.Room;
import org.chat.domain.service.RoomService;
import org.chat.security.StompPrincipal;
import org.common.utils.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/chat/rooms")
public class ChatController {

    private final RoomService roomService;

    // 방 생성
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createRoom(@AuthenticationPrincipal StompPrincipal me,
                                        @RequestBody CreateRoomRequest request){
        RoomUserResponse roomUserResponse = roomService.createRoom(me.getUserId(), request.targetId());
        log.info("사용자 ID 값 제대로 들어오는 지????? : " + me.getName() , request.targetId());
        SuccessResponse response = new SuccessResponse(true,"채팅방 생성 성공",roomUserResponse.roomId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 별명으로 방 조회
    // todo 그냥 키워드로 방 조회
    @GetMapping("/{nickname}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> findRoom(@PathVariable String nickname){
        RoomResponse room = roomService.findRoomByName(nickname);
        SuccessResponse response = new SuccessResponse(true,"별명으로 채팅방 조회 성공",room);
        return new ResponseEntity<>(response,HttpStatus.FOUND);
    }

    // 방 삭제
    @DeleteMapping("/{roomId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteRoom(@PathVariable String roomId){
        roomService.deleteRoom(roomId);
        SuccessResponse response = new SuccessResponse(true,"채팅방 삭제 성공",roomId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
