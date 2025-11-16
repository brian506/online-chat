package org.user.domain.dto.response;

import org.user.domain.dto.request.CreateUserRequest;

import java.time.LocalDate;


public record SignUpUserResponse(String userId, String nickname, LocalDate birth) {
    public static SignUpUserResponse requestToResponse(CreateUserRequest request,String userId){
        return new SignUpUserResponse(userId,request.nickname(), request.birth());
    }
}
