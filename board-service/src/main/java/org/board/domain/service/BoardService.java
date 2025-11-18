package org.board.domain.service;

import lombok.RequiredArgsConstructor;
import org.board.domain.dto.request.CreateBoardRequest;
import org.board.domain.dto.response.BoardResponse;
import org.board.domain.dto.response.UserResponse;
import org.board.domain.entity.Board;
import org.board.domain.entity.Tags;
import org.board.domain.entity.SortType;
import org.board.domain.repository.QuestionRepository;
import org.common.utils.ErrorMessages;
import org.common.utils.OptionalUtil;
import org.common.utils.SecurityUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final QuestionRepository questionRepository;
    private final UserServiceClient userServiceClient;

    // 질문 게시글 작성
    @Transactional
    public String postBoard(final CreateBoardRequest createBoardRequest){
        String userId = SecurityUtil.getCurrentUserId();
        UserResponse userResponse = userServiceClient.getUser(userId);

        Board board = Board.toQuestionEntity(createBoardRequest,userResponse);
        questionRepository.save(board);
        return board.getId();
    }

     // 모든 질문들 불러오기 - cursor
    @Transactional(readOnly = true)
    public Slice<BoardResponse> getQuestionsByCursor(
            Tags tags, SortType sortType, Integer cursorValue, LocalDateTime cursorCreatedAt, int size) {
        Pageable pageable = PageRequest.of(0,size); // 페이지 번호는 필요없고(0), size 는 프론트에서 결정
        return questionRepository.getQuestionsByCursor(tags,sortType,cursorValue,cursorCreatedAt,pageable);
    }

    // 주제별 질문들 조회

    // 특정 게시글 조회
    @Transactional(readOnly = true)
    public BoardResponse findQuestion(final String questionId){
        Board board = OptionalUtil.getOrElseThrow(questionRepository.findById(questionId), ErrorMessages.POST_NOT_FOUND);
        board.increaseViewCount();
        return Board.toQuestionDto(board);
    }

//    // 제목 조회 - 연관검색어?
//    public QuestionResponse findQuestionByTitle(final String title){
//        Question question = OptionalUtil.getOrElseThrow(questionRepository.findByTitle(title),ErrorMessages.POST_NOT_FOUND);
//
//    }

    // 조회수 카운트

    // 답변수 카운트
}
