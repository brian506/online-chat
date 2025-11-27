package org.user.domain.dto.event;

import org.user.domain.dto.request.WhiskyFavoritesRequest;
import org.user.domain.dto.response.WhiskyFavoritesResponse;
import org.user.domain.entity.ActionType;

public record UserWhiskyFavoritesEvent( String whiskyId,String userId, ActionType actionType) {
    public static UserWhiskyFavoritesEvent fromResponse(WhiskyFavoritesResponse response,String userId,ActionType actionType){
        return new UserWhiskyFavoritesEvent(
                response.whiskyId(),
                userId,
                actionType
        );
    }
    public static UserWhiskyFavoritesEvent fromRequest(String whiskyId, String userId,ActionType actionType){
        return new UserWhiskyFavoritesEvent(
                whiskyId,
                userId,
                actionType
        );
    }
}
