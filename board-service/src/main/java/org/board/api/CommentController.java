package org.board.api;

import lombok.RequiredArgsConstructor;
import org.board.domain.dto.request.CreateBoardRequest;
import org.board.domain.dto.request.CreateCommentRequest;
import org.board.domain.dto.response.BoardResponse;
import org.board.domain.dto.response.CommentResponse;
import org.board.domain.entity.Tags;
import org.board.domain.entity.SortType;
import org.board.domain.service.BoardService;
import org.board.domain.service.CommentService;
import org.common.utils.SuccessMessages;
import org.common.utils.SuccessResponse;
import org.springframework.data.domain.Slice;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final BoardService boardService;
    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> postComment(final CreateCommentRequest request){
        CommentResponse commentResponse = commentService.postComment(request);
        SuccessResponse response = new SuccessResponse<>(true,SuccessMessages.COMMENT_CREATE_SUCCESS,commentResponse);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }



}
