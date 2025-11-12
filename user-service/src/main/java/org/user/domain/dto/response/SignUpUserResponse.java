package org.user.domain.dto.response;

import org.user.domain.dto.request.CreateUserRequest;
import org.user.domain.entity.Job;

public record SignUpUserResponse(Long userId,String nickname, String birth, String major, Job job) {
    public static SignUpUserResponse requestToResponse(CreateUserRequest request,Long userId){
        return new SignUpUserResponse(userId,request.nickname(), request.birth(), request.major(), request.job());
    }
}
