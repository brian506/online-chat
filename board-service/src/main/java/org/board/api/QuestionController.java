package org.board.api;

import lombok.RequiredArgsConstructor;
import org.board.domain.dto.request.CreateQuestionRequest;
import org.board.domain.dto.response.QuestionResponse;
import org.board.domain.service.QuestionService;
import org.common.utils.SuccessMessages;
import org.common.utils.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    // 질문 게시글 작성
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> postQuestion(@RequestBody CreateQuestionRequest request){
        UUID questionId = questionService.postBoard(request);
        SuccessResponse response = new SuccessResponse(true, SuccessMessages.POST_CREATE_SUCCESS,questionId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 질문 단일 조회
    @GetMapping("/{questionId}")
    public ResponseEntity<?> findQuestion(@PathVariable UUID questionId){
        QuestionResponse questionResponse = questionService.findQuestion(questionId);
        SuccessResponse response = new SuccessResponse(true,SuccessMessages.ID_POSTS_RETRIEVE_SUCCESS,questionResponse);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
