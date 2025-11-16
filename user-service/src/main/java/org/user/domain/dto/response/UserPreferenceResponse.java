package org.user.domain.dto.response;

import org.user.domain.entity.Level;
import org.user.domain.entity.Taste;
import org.user.domain.entity.User;

public record UserPreferenceResponse(String userId, Level level, Taste taste) {
    public static UserPreferenceResponse from(User user){
        return new UserPreferenceResponse(
                user.getId(),
                user.getLevel(),
                user.getTaste()
        );
    }
}
