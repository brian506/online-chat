package org.user.domain.dto.request;

public record AuthRegisterRequest(String email,String password,String nickname) {
    public static AuthRegisterRequest toCreateUser(CreateUserRequest request){
        return new AuthRegisterRequest(request.email(), request.password(), request.nickname());
    }
}
