package org.user.domain.dto.response;

import lombok.Builder;
import lombok.Getter;


public record UserResponse(String userId,String nickname,String birth) {
}
