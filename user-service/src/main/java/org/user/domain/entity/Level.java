package org.user.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Level {
    BEGINNER("초급자"),
    INTERMEDIATE("중급자"),
    ADVANCED("고급자");

    private final String description;
}
