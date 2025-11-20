package org.user.domain.dto.response;

import org.user.domain.entity.Follow;
import org.user.domain.entity.Grade;
import org.user.domain.entity.Level;
import org.user.domain.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public record UserResponse(String userId,String nickname, LocalDate birth, Level level, Grade grade, long followerCount, long followingCount, boolean followingByMe) {
    public static UserResponse userResponseToDto(User user,long followerCount, long followingCount,boolean followingByMe) {
        return new UserResponse(
                user.getId(),
                user.getNickname(),
                user.getBirth(),
                user.getLevel(),
                user.getGrade(),
                followerCount,
                followingCount,
                followingByMe
        );
    }
}
