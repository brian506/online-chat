package org.user.domain.dto.response;

import java.util.UUID;

public record AuthRegisterResponse(String userId, String email,String nickname) {
}
