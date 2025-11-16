package org.board.api;

import lombok.RequiredArgsConstructor;
import org.board.domain.dto.request.CreateQuestionRequest;
import org.board.domain.dto.response.QuestionResponse;
import org.board.domain.entity.BoardTopic;
import org.board.domain.entity.SortType;
import org.board.domain.service.QuestionService;
import org.common.utils.SuccessMessages;
import org.common.utils.SuccessResponse;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
        String questionId = questionService.postBoard(request);
        SuccessResponse response = new SuccessResponse(true, SuccessMessages.POST_CREATE_SUCCESS,questionId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 게시판 별(답변순,조회순,오래된순,최신순) 조회
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getQuestions(@RequestParam BoardTopic boardTopic,
                                          @RequestParam(defaultValue = "LATEST") SortType sortType,
                                          // 마지막 글의 답변 수/조회수
                                          @RequestParam(required = false) Integer cursorValue,
                                          // 마지막 글의 생성일시
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorCreatedAt,
                                          @RequestParam(defaultValue = "10") int size){
        Slice<QuestionResponse> questionResponses = questionService.getQuestionsByCursor(boardTopic, sortType, cursorValue, cursorCreatedAt, size);
        SuccessResponse response = new SuccessResponse(true,SuccessMessages.ALL_POSTS_RETRIEVE_SUCCESS,questionResponses);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // 질문 단일 조회
    @GetMapping("/{questionId}")
    public ResponseEntity<?> findQuestion(@PathVariable String questionId){
        QuestionResponse questionResponse = questionService.findQuestion(questionId);
        SuccessResponse response = new SuccessResponse(true,SuccessMessages.ID_POSTS_RETRIEVE_SUCCESS,questionResponse);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
