package org.board.domain.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BoardTopic {
    UNIVERSITY("대학생활 게시판"),
    ROMANCE("연애 게시판"),
    CAREER("진로 게시판"),
    POLITICS("정치 게시판");

    private final String description;
}
