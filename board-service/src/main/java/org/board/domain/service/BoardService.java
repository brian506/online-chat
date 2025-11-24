package org.board.domain.service;

import lombok.RequiredArgsConstructor;
import org.board.domain.dto.request.CreateBoardRequest;
import org.board.domain.dto.response.BoardResponse;
import org.board.domain.dto.response.CommentResponse;
import org.board.domain.dto.response.UserResponse;
import org.board.domain.entity.Board;
import org.board.domain.entity.Comment;
import org.board.domain.entity.SortType;
import org.board.domain.repository.BoardRepository;
import org.board.utils.FileService;
import org.common.utils.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final FileService fileService;
    private final CommentService commentService;

    // 위스키 게시글 작성
    @Transactional
    public String postBoard(final CreateBoardRequest createBoardRequest,final MultipartFile image){
        UserPrincipal loginUser = SecurityUtil.getCurrentUser();

        Board board = Board.toBoardEntity(createBoardRequest,loginUser);
        String url = uploadImage(image);
        board.setImageUrl(url);
        boardRepository.save(board);
        return board.getId();
    }

     // 즐겨찾기한 위스키 게시물 불러오기 - cursor
    @Transactional(readOnly = true)
    public Slice<BoardResponse> getFavoritesBoardsByCursor(
             SortType sortType, Integer cursorValue, LocalDateTime cursorCreatedAt, int size) {
        String userId = SecurityUtil.getCurrentUserId();
        Pageable pageable = PageRequest.of(0,size); // 페이지 번호는 필요없고(0), size 는 프론트에서 결정
        return boardRepository.getFavoritesBoardsByCursor(userId,sortType,cursorValue,cursorCreatedAt,pageable);
    }

    // 팔로잉한 사람의 위스키 게시물 불러오기 - cursor
    @Transactional(readOnly = true)
    public Slice<BoardResponse> getFollowingBoardsByCursor(
            SortType sortType, Integer cursorValue, LocalDateTime cursorCreatedAt, int size) {
        String userId = SecurityUtil.getCurrentUserId();
        Pageable pageable = PageRequest.of(0,size); // 페이지 번호는 필요없고(0), size 는 프론트에서 결정
        return boardRepository.getFollowingBoardsByCursor(userId,sortType,cursorValue,cursorCreatedAt,pageable);
    }

    // 특정 게시글 조회
    @Transactional(readOnly = true)
    public BoardResponse getBoard(final String boardId){
        Board board = OptionalUtil.getOrElseThrow(boardRepository.findById(boardId), ErrorMessages.POST_NOT_FOUND);
        List<CommentResponse> commentResponses = commentService.getComments(boardId);
        board.increaseViewCount();
        return BoardResponse.from(board,commentResponses);
    }




    // S3 파일 업로드
    private String uploadImage(final MultipartFile image){
        String url = fileService.toUrls(image);
        fileService.fileUpload(image, url);
        return url;
    }

}
