package org.user.domain.dto.request;

import org.user.domain.entity.Gender;

import java.time.LocalDate;


public record CreateUserRequest(String email, String password, String nickname, LocalDate birth,  Gender gender) {
}
