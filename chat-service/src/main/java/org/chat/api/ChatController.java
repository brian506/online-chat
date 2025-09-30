package org.chat.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.request.CreateRoomRequest;
import org.chat.domain.entity.Room;
import org.chat.domain.service.ChatService;
import org.common.utils.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/rooms")
    public ResponseEntity<?> createRoom(@RequestBody CreateRoomRequest request){
        Room room = chatService.createRoom(request);
        SuccessResponse response = new SuccessResponse(true,"채팅방 생성 성공",room);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}
