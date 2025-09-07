package org.auth.security.dto.response;

import org.auth.domain.entity.Role;

import java.util.Date;

public record AccessTokenPayload(String email, Role role, Date date) {
}
