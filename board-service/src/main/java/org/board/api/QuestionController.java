package org.board.api;

import lombok.RequiredArgsConstructor;
import org.board.domain.dto.request.CreateBoardRequest;
import org.board.domain.dto.response.BoardResponse;
import org.board.domain.entity.Tags;
import org.board.domain.entity.SortType;
import org.board.domain.service.BoardService;
import org.common.utils.SuccessMessages;
import org.common.utils.SuccessResponse;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final BoardService boardService;

    // 질문 게시글 작성
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> postQuestion(@RequestBody CreateBoardRequest request){
        String questionId = boardService.postBoard(request);
        SuccessResponse response = new SuccessResponse(true, SuccessMessages.POST_CREATE_SUCCESS,questionId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 게시판 별(답변순,조회순,오래된순,최신순) 조회
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getQuestions(@RequestParam Tags tags,
                                          @RequestParam(defaultValue = "LATEST") SortType sortType,
                                          // 마지막 글의 답변 수/조회수
                                          @RequestParam(required = false) Integer cursorValue,
                                          // 마지막 글의 생성일시
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorCreatedAt,
                                          @RequestParam(defaultValue = "10") int size){
        Slice<BoardResponse> questionResponses = boardService.getQuestionsByCursor(tags, sortType, cursorValue, cursorCreatedAt, size);
        SuccessResponse response = new SuccessResponse(true,SuccessMessages.ALL_POSTS_RETRIEVE_SUCCESS,questionResponses);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // 질문 단일 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<?> findQuestion(@PathVariable String questionId){
        BoardResponse boardResponse = boardService.findQuestion(questionId);
        SuccessResponse response = new SuccessResponse(true,SuccessMessages.ID_POSTS_RETRIEVE_SUCCESS, boardResponse);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
