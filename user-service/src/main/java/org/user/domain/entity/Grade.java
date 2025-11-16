package org.user.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Grade {
    NEWBIE ("뉴비"),
    MEMBER ("멤버"),
    ACTIVE ("액티브"),
    EXPERT ("전문가"),
    MASTER ("마스터");
    private final String description;
}
