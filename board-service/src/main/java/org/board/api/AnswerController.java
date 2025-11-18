package org.board.api;

import lombok.RequiredArgsConstructor;
import org.board.domain.dto.request.CreateCommentRequest;
import org.board.domain.service.CommentService;
import org.common.utils.SuccessMessages;
import org.common.utils.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/v1/api/answers")
@RestController
public class AnswerController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<?> postAnswer(@RequestBody CreateCommentRequest request){
        String answerId = commentService.postAnswer(request);
        SuccessResponse response = new SuccessResponse(true, SuccessMessages.ANSWER_CREATE_SUCCESS,answerId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{answerId}")
    public ResponseEntity<?> findAnswer(@PathVariable String answerId){
        AnswerResponse answerResponse = commentService.findAnswer(answerId);
        SuccessResponse response = new SuccessResponse(true,SuccessMessages.ANSWER_RETRIEVE_SUCCESS,answerResponse);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @GetMapping("/chat-room/{answerId")
    public ResponseEntity<?> getChatRoomInfo(@PathVariable String answerId){
        AnswerChatResponse chatResponse = commentService.getChatAnswerInfo(answerId);
        SuccessResponse response = new SuccessResponse(true,SuccessMessages.ANSWER_RETRIEVE_SUCCESS,chatResponse);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
