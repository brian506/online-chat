package org.board.domain.repository.custom;

import org.board.domain.dto.response.QuestionResponse;
import org.board.domain.entity.BoardTopic;
import org.board.domain.entity.SortType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

public interface QuestionRepositoryCustom {

    /**
     *
     * @param sortType - 정렬 조건
     * @param cursorValue - 프론트가 직전에 받은 페이지의 마지막 답변수 또는 조회수 값
     * @param cursorCreatedAt - 프론트가 직전에 받은 페이지의 마지막 게시글의 생성일자
     * @return
     */
    // 해당 게시판의 모든 질문들 불러오기
    Slice<QuestionResponse> getQuestionsByCursor(BoardTopic boardTopic, SortType sortType, Integer cursorValue, LocalDateTime cursorCreatedAt, Pageable pageable);
}

