package org.common.event;


import org.common.enums.ActionType;

public record FollowEvent(String followerId, String followingId, String followerNickname,ActionType actionType) {
    public static FollowEvent toEvent(String followerId,String followingId,String followerNickname, ActionType actionType){
        return new FollowEvent(
                followerId, followingId, followerNickname, actionType);
    }
}
