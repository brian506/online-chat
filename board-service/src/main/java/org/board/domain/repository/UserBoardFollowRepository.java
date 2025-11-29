package org.board.domain.repository;

import org.board.domain.entity.BoardUserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBoardFollowRepository extends JpaRepository<BoardUserFollow,String> {
    void deleteByFollowingIdAndFollowerId(String followingId, String followerId);
}
