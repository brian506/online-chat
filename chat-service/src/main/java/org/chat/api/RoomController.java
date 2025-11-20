package org.chat.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.request.CreateRoomRequest;
import org.chat.domain.dto.response.RoomListResponse;
import org.chat.domain.dto.response.RoomResponse;
import org.chat.domain.dto.response.RoomUserResponse;
import org.chat.domain.entity.UserType;
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
@RequestMapping("/v1/api/chat/rooms")
public class RoomController {

    private final RoomService roomService;

    // 방 생성
//    @PostMapping
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<?> createRoom(@AuthenticationPrincipal StompPrincipal me,
//                                        @RequestBody CreateRoomRequest request){
//        RoomUserResponse roomUserResponse = roomService.createPrivateRoom(request.answerId());
//        log.info("createRoom me={}, target={}", me.getName(), request.answerId());
//        SuccessResponse response = new SuccessResponse(true,"채팅방 생성 성공",roomUserResponse);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    // 내 채팅방 목록 조회
    @GetMapping("/my-rooms/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyRooms(@PathVariable String userId,
                                       @RequestParam UserType userType,
                                       @RequestParam(required = false) String cursor){
        RoomListResponse roomResponses = roomService.findRoomsByUsertype(userId,userType,cursor);
        SuccessResponse response = new SuccessResponse(true,"내 채팅방 조회 성공",roomResponses);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // 별명으로 방 조회
    // todo 그냥 키워드로 방 조회
    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> findRoom(@AuthenticationPrincipal StompPrincipal me,
                                      @RequestParam("name") String name){
        RoomResponse room = roomService.findRoomByName(me.getUserId(),name);
        SuccessResponse response = new SuccessResponse(true,"별명으로 채팅방 조회 성공",room);
        return new ResponseEntity<>(response,HttpStatus.OK);
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
