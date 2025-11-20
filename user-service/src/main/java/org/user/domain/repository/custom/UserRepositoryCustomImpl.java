package org.user.domain.repository.custom;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.user.domain.dto.response.UserResponse;
import org.user.domain.entity.QFollow;
import org.user.domain.entity.QUser;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;
    private final QFollow follow = QFollow.follow;

    // 서브쿼리용 별칭
    private final QFollow subFollower = new QFollow("subFollower");
    private final QFollow subFollowing = new QFollow("subFollowing");
    private final QFollow subMe = new QFollow("subMe");

    // 사용자 정보 조회 - 팔로잉 수, 팔로워 수, 맞팔 여부 / 사용자 정보
    @Override
    public UserResponse getUserInfo(String userId,String loginUserId) {
        return queryFactory
                .select(Projections.constructor(UserResponse.class,
                        user.id,
                        user.nickname,
                        user.birth,
                        getFollowerCount(),
                        getFollowingCount(),
                        isFollowing(loginUserId)
                ))
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();
    }

    // 팔로워 수 집계
    private JPQLQuery<Long> getFollowerCount(){
        return JPAExpressions
                .select(subFollower.count())
                .from(subFollower)
                .where(subFollower.following.id.eq(user.id));
    }

    // 팔로잉 수 집계
    private JPQLQuery<Long> getFollowingCount(){
        return JPAExpressions
                .select(subFollowing.count())
                .from(subFollowing)
                .where(subFollowing.follower.id.eq(user.id));
    }

    // 맞팔 여부 - 나의 팔로잉,팔로워 명단과 있는지 비교
    private Expression<Boolean> isFollowing(String loginUserId){
        if(loginUserId == null){
            return Expressions.asBoolean(false);
        }
        return new CaseBuilder()
                .when(JPAExpressions
                        .selectOne()
                        .from(subMe)
                        .where(subMe.follower.id.eq(loginUserId)
                                .and(subMe.following.id.eq(user.id)))
                        .exists())
                .then(true)
                .otherwise(false);

    }
}
