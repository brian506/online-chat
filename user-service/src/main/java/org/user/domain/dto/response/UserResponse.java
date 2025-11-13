package org.user.domain.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;


public record UserResponse(UUID userId, String nickname, String birth) {
}
