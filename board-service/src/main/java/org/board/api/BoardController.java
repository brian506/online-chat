package org.board.api;

import lombok.RequiredArgsConstructor;
import org.board.domain.dto.request.CreateBoardRequest;
import org.board.domain.dto.request.CreateCommentRequest;
import org.board.domain.dto.response.BoardResponse;
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

@RequiredArgsConstructor
@RequestMapping("/v1/api/boards")
@RestController
public class BoardController {

    private final BoardService boardService;

    // 게시글 작성
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> postComment(@RequestPart CreateBoardRequest request, @RequestPart MultipartFile file) {
        String boardId = boardService.postBoard(request, file);
        SuccessResponse response = new SuccessResponse(true, SuccessMessages.POST_CREATE_SUCCESS, boardId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 위스키 즐겨찾기한 피드 조회 - 커서
    @GetMapping("/favorite-whiskies")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getFavoriteBoards(@RequestParam(name = "sortType", defaultValue = "LATEST") SortType sortType,
                                       @RequestParam(name = "cursorValue", required = false) Integer cursorValue,
                                       @RequestParam(name = "cursorCreatedAt", required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorCreatedAt,
                                       @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Slice<BoardResponse> boardResponses = boardService.getFavoritesBoardsByCursor(sortType, cursorValue, cursorCreatedAt, size);
        SuccessResponse response = new SuccessResponse(true, SuccessMessages.ID_POSTS_RETRIEVE_SUCCESS, boardResponses);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 팔로잉한 사람들의 피드 조회 - 커서
    @GetMapping("/followings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getFollowingBoards(@RequestParam(name = "sortType", defaultValue = "LATEST") SortType sortType,
                                       @RequestParam(name = "cursorValue", required = false) Integer cursorValue,
                                       @RequestParam(name = "cursorCreatedAt", required = false)
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorCreatedAt,
                                       @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Slice<BoardResponse> boardResponses = boardService.getFollowingBoardsByCursor(sortType, cursorValue, cursorCreatedAt, size);
        SuccessResponse response = new SuccessResponse(true, SuccessMessages.ID_POSTS_RETRIEVE_SUCCESS, boardResponses);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 게시물 단일 조회
    @GetMapping("/{boardId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getBoard(@PathVariable String boardId){
        BoardResponse boardResponse = boardService.getBoard(boardId);
        SuccessResponse response = new SuccessResponse(true, SuccessMessages.ID_POSTS_RETRIEVE_SUCCESS, boardResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
