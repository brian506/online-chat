package org.auth.domain.repository.custom;

import org.auth.domain.entity.LoginStatus;
import org.auth.domain.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UserRepositoryCustom {
    // PENDING 상태의 user 를 ACTIVATE 로 변경하고, expiredAt 을 null 로 변환
    long activatePendingUsers(List<UUID> userIds, LoginStatus newStatus,LoginStatus curStatus);
    // 만료된 사용자 조회
    List<User> findExpiredPendingUsers(LocalDateTime now);
}
