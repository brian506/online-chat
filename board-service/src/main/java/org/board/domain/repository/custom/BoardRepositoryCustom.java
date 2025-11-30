package org.board.domain.repository.custom;

import org.board.domain.dto.response.BoardResponse;
import org.board.domain.entity.SortType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

public interface BoardRepositoryCustom {

    /**
     *
     * @param sortType - 정렬 조건
     * @param cursorValue - 프론트가 직전에 받은 페이지의 마지막 답변수 또는 조회수 값
     * @param cursorCreatedAt - 프론트가 직전에 받은 페이지의 마지막 게시글의 생성일자
     * @return
     */
    // 관심등록한 위스키 게시물 조회
    Slice<BoardResponse> getFavoritesBoardsByCursor(String userId,  SortType sortType, Integer cursorValue, LocalDateTime cursorCreatedAt, String cursorId, Pageable pageable);
    Slice<BoardResponse> getFollowingBoardsByCursor(String userId,  SortType sortType, Integer cursorValue, LocalDateTime cursorCreatedAt, String cursorId, Pageable pageable);
}

