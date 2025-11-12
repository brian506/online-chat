package org.qboard.domain.service;

import lombok.RequiredArgsConstructor;
import org.qboard.domain.dto.request.PostBoardRequest;
import org.qboard.domain.dto.response.PostBoardResponse;
import org.qboard.domain.repository.BoardRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    // 질문 게시글 작성
    public PostBoardResponse postBoard(final PostBoardRequest postBoardRequest,final Long userId){

    }
    // 제목 조회

    // 주제별 게시판 조회

    // 특정 게시글 조회

    // 조회수 카운트

    // 답변수 카운트
}
