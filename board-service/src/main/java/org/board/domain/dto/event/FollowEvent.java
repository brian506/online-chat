package org.board.domain.dto.event;

import org.board.domain.entity.ActionType;

public record FollowEvent(String followerId, String followingId, ActionType actionType) {
}
