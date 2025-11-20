package org.user.domain.repository.custom;

import org.user.domain.dto.response.UserResponse;

public interface UserRepositoryCustom {
    UserResponse getUserInfo(String userId,String loginUserId);
}
