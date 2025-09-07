package org.auth.security.dto.response;

import org.auth.domain.entity.Role;

import java.util.Date;

public record RefreshTokenPayload(String email, Date date) {
}
