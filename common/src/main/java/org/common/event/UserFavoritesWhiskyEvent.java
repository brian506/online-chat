package org.common.event;


import org.common.enums.ActionType;

public record UserFavoritesWhiskyEvent(String userId, String whiskyId, ActionType actionType) {
    public static UserFavoritesWhiskyEvent toEvent(String userId,String whiskyId,ActionType actionType){
        return new UserFavoritesWhiskyEvent(
                userId, whiskyId, actionType);
    }
}
