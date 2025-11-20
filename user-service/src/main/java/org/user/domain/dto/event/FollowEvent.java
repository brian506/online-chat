package org.user.domain.dto.event;


import org.user.domain.entity.ActionType;

public record FollowEvent(String followerId, String followingId, ActionType actionType) {
    public static FollowEvent toEvent(String followerId, String followingId,ActionType actionType){
        return new FollowEvent(
                followerId,
                followingId,
                actionType
        );
    }
}
