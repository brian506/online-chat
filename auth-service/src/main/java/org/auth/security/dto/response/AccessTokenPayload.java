package org.auth.security.dto.response;

import org.auth.domain.entity.Role;

import java.util.Date;


public record AccessTokenPayload(String userId, String email, Role role, Date date) {
}
