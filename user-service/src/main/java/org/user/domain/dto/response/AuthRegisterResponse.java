package org.user.domain.dto.response;

import java.util.UUID;

public record AuthRegisterResponse(UUID userId, String email) {
}
