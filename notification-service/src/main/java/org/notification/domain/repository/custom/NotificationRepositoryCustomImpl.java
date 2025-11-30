package org.notification.domain.repository.custom;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.notification.domain.entity.Notification;
import org.notification.domain.entity.QNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QNotification notification = QNotification.notification;

    @Override
    public Slice<Notification> getNotificationsByCursor(
            String userId,
            String cursorId,
            LocalDateTime cursorCreatedAt,
            Pageable pageable
    ) {

        List<Notification> content = queryFactory
                .selectFrom(notification)
                .where(
                        notification.receiverId.eq(userId), // 내 알림만
                        ltCursor(cursorId, cursorCreatedAt) // ★ 핵심: 동적 커서 조건
                )
                .orderBy(
                        notification.createdAt.desc(), // 1차 정렬: 최신순
                        notification.id.asc()          // 2차 정렬: ID 오름차순 (동점자 처리)
                )
                .limit(pageable.getPageSize() + 1) // 다음 페이지 확인용 (+1)
                .fetch();

        // 2. Slice 변환 (다음 페이지 여부 확인)
        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize()); // +1개 가져온 건 제거
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    /**
     * (생성일 < 커서시간) OR (생성일 == 커서시간 AND ID > 커서ID)
     */
    private BooleanExpression ltCursor(String cursorId, LocalDateTime cursorCreatedAt) {
        // 1. 첫 페이지 요청 (커서 정보가 없음) -> 조건 없이 최신순 조회
        if (cursorId == null || cursorCreatedAt == null) {
            return null;
        }

        // 2. 다음 페이지 요청 (커서 정보 있음) -> 복합 조건 적용
        return notification.createdAt.lt(cursorCreatedAt)
                .or(
                        notification.createdAt.eq(cursorCreatedAt)
                                .and(notification.id.gt(cursorId)) // ID는 ASC 정렬이므로 GT(큰값)가 다음 데이터
                );
    }
}

