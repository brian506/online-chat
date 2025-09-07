package org.auth.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {
    GENERAL("ROLE_GENERAL"),
    ADMIN("ROLE_ADMIN");

    private final String key;
}
