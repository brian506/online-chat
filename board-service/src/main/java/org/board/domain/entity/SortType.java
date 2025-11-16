package org.board.domain.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SortType {
    POPULAR("인기순"),
    VIEWS("조회순"),
    OLD("오래된 순"),
    LATEST("최신순");

    private final String description;
}
