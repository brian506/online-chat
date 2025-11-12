package org.auth.domain.dto.response;

import org.auth.domain.entity.Role;

import java.util.Date;
import java.util.UUID;

public record RefreshTokenPayload(UUID userId, Date date) {
}
