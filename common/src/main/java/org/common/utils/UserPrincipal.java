package org.common.utils;

public record UserPrincipal(String userId, String nickname) {
    public static UserPrincipal of(String userId, String nickname){
        return new UserPrincipal(
                userId,
                nickname
        );
    }
}
