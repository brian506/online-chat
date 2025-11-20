package org.board.domain.dto.event;

import org.board.domain.entity.ActionType;

public record UserFavoritesEvent(String userId, String whiskyId, ActionType actionType) {
}
