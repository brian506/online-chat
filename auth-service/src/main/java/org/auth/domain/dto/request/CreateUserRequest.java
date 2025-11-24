package org.auth.domain.dto.request;

public record CreateUserRequest(String email,String password,String nickname) {
}
