package org.board.api;

import lombok.RequiredArgsConstructor;
import org.board.domain.dto.request.CreateAnswerRequest;
import org.board.domain.dto.response.AnswerResponse;
import org.board.domain.service.AnswerService;
import org.common.utils.SuccessMessages;
import org.common.utils.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/v1/api/answers")
@RestController
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    public ResponseEntity<?> postAnswer(@RequestBody CreateAnswerRequest request){
        UUID answerId = answerService.postAnswer(request);
        SuccessResponse response = new SuccessResponse(true, SuccessMessages.ANSWER_CREATE_SUCCESS,answerId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{answerId}")
    public ResponseEntity<?> getAnswer(@PathVariable UUID answerId){
        AnswerResponse answerResponse = answerService.findAnswer(answerId);
        SuccessResponse response = new SuccessResponse(true,SuccessMessages.ANSWER_RETRIEVE_SUCCESS,answerResponse);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
