package org.user.domain.dto.event;

import org.user.domain.dto.request.WhiskyFavoritesRequest;
import org.user.domain.dto.response.WhiskyFavoritesResponse;
import org.user.domain.entity.ActionType;

public record UserWhiskyFavoritesEvent(String userId, String whiskyId, ActionType actionType) {
    public static UserWhiskyFavoritesEvent fromResponse(WhiskyFavoritesResponse response,String userId,ActionType actionType){
        return new UserWhiskyFavoritesEvent(
                userId,
                response.whiskyId(),
                actionType
        );
    }
    public static UserWhiskyFavoritesEvent fromRequest(WhiskyFavoritesRequest request, String userId,ActionType actionType){
        return new UserWhiskyFavoritesEvent(
                request.whiskyId(),
                userId,
                actionType
        );
    }
}
