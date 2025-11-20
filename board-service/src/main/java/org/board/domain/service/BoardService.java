package org.board.domain.service;

import lombok.RequiredArgsConstructor;
import org.board.domain.dto.request.CreateBoardRequest;
import org.board.domain.dto.response.BoardResponse;
import org.board.domain.dto.response.UserResponse;
import org.board.domain.entity.Board;
import org.board.domain.entity.SortType;
import org.board.domain.repository.BoardRepository;
import org.board.utils.FileService;
import org.common.utils.ErrorMessages;
import org.common.utils.OptionalUtil;
import org.common.utils.SecurityUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserServiceClient userServiceClient;
    private final FileService fileService;

    // 위스키 게시글 작성
    @Transactional
    public String postBoard(final CreateBoardRequest createBoardRequest,final MultipartFile image){
        String userId = SecurityUtil.getCurrentUserId();
        UserResponse userResponse = userServiceClient.getUser(userId);

        Board board = Board.toBoardEntity(createBoardRequest,userResponse);
        String url = uploadImage(image);
        board.setImageUrl(url);
        boardRepository.save(board);
        return board.getId();
    }

     // 모든 질문들 불러오기 - cursor
    @Transactional(readOnly = true)
    public Slice<BoardResponse> getBoardsByCursor(
             SortType sortType, Integer cursorValue, LocalDateTime cursorCreatedAt, int size) {
        String userId = SecurityUtil.getCurrentUserId();
        Pageable pageable = PageRequest.of(0,size); // 페이지 번호는 필요없고(0), size 는 프론트에서 결정
        return boardRepository.getBoardsByCursor(userId,sortType,cursorValue,cursorCreatedAt,pageable);
    }


    // 특정 게시글 조회
    @Transactional(readOnly = true)
    public BoardResponse getBoard(final String boardId){
        Board board = OptionalUtil.getOrElseThrow(boardRepository.findById(boardId), ErrorMessages.POST_NOT_FOUND);
        board.increaseViewCount();
        return BoardResponse.from(board);
    }

//    // 제목 조회 - 연관검색어?
//    public QuestionResponse findQuestionByTitle(final String title){
//        Question question = OptionalUtil.getOrElseThrow(questionRepository.findByTitle(title),ErrorMessages.POST_NOT_FOUND);
//
//    }

    // 조회수 카운트

    // 답변수 카운트



    // S3 파일 업로드
    private String uploadImage(final MultipartFile image){
        String url = fileService.toUrls(image);
        fileService.fileUpload(image, url);
        return url;
    }

}
