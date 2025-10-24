package org.auth.domain.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.auth.domain.entity.LoginStatus;
import org.auth.domain.entity.QUser;
import org.auth.domain.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom{

    private final JPAQueryFactory query;
    private final QUser user = QUser.user;

    @Override
    public long activatePendingUsers(List<UUID> userIds, LoginStatus newStatus, LoginStatus curStatus) {
        return query
                .update(user)
                .set(user.status,newStatus)
                .setNull(user.expiredAt)
                .where(
                        user.id.in(userIds),
                        user.status.eq(curStatus)
                )
                .execute();
    }
    @Override
    public List<User> findExpiredPendingUsers(LocalDateTime now) {
        QUser user = QUser.user;
        return query
                .selectFrom(user)
                .where(
                        user.status.eq(LoginStatus.PENDING),
                        user.expiredAt.lt(now) // less than
                )
                .fetch();
    }
}
