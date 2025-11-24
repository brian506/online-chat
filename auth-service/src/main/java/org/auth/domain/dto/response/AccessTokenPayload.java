package org.auth.domain.dto.response;

import org.auth.domain.entity.Role;

import java.util.Date;


public record AccessTokenPayload(String userId, String nickname, Role role, Date date) {
}
