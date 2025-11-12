package org.user.domain.dto.request;

public record AuthRegisterRequest(String email,String password) {
    public static AuthRegisterRequest toCreateUser(CreateUserRequest request){
        return new AuthRegisterRequest(request.email(), request.password());
    }
}
