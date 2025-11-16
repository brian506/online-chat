package org.user.domain.dto.response;

import org.user.domain.entity.User;

import java.time.LocalDate;
import java.util.UUID;


public record UserResponse(String userId, String nickname, LocalDate birth) {
    public static UserResponse userResponseToDto(User user) {
        return new UserResponse(
                user.getId(),
                user.getNickname(),
                user.getBirth()
        );
    }
}
