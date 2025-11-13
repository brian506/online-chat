package org.chat.api;

import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.chat.domain.dto.response.MessageListResponse;
import org.chat.domain.dto.response.MessageResponse;
import org.chat.domain.service.MessageService;
import org.common.utils.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/v1/api/chat/message")
@RestController
public class MessageController {

    private final MessageService messageService;

    // 채팅방의 대화 내용 조회 - List 기반
//    @GetMapping("/{roomId}")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<?> getMessagesFromRoom(@PathVariable String roomId) {
//        List<MessageResponse> messageResponses = messageService.getMessagesFromRoom(roomId);
//        SuccessResponse response = new SuccessResponse(true,"채팅방 메시지 불러오기 성공",messageResponses);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    // 채팅방의 대화 내용 조회 - cursor 기반
    @GetMapping("/{roomId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMessagesFromRoom(@PathVariable String roomId,
                                                 @RequestParam(required = false) String cursor){
        MessageListResponse messageResponses = messageService.getMessagesFromRoomCursor(roomId,cursor);
        SuccessResponse response = new SuccessResponse(true,"채팅방 메시지 불러오기 성공",messageResponses);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
