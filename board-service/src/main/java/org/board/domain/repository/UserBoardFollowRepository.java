package org.board.domain.repository;

import org.board.domain.entity.UserBoardFollow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBoardFollowRepository extends JpaRepository<UserBoardFollow,String> {
    void deleteByFollowingIdAndFollowerId(String followingId, String followerId);
}
