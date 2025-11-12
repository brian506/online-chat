package org.user.domain.dto.request;

import org.user.domain.entity.Job;

public record CreateUserRequest(String email,String password,String nickname, String birth, String major, Job job) {
}
