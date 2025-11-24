package org.user.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.user.domain.entity.Follow;



public interface FollowRepository extends JpaRepository<Follow,String> {
    boolean existsByFollower_IdAndFollowing_Id(String followerId, String followingId);

    void deleteByFollower_IdAndFollowing_Id(String followerId, String followingId);


}
