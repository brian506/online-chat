package org.user.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.user.domain.entity.Follow;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow,String> {
    boolean existsByFollower_IdAndFollowing_Id(String followerId, String followingId);

    void deleteByFollower_IdAndFollowing_Id(String followerId, String followingId);

    long countByFollower_Id(String followerId);
    long countByFollowing_Id(String followingId);

    List<Follow> findFollowerByUserId(String userId);
    List<Follow> findFollowingByUserId(String userId);

}
