package org.common.utils;


import org.common.exception.custom.AuthenticationException;
import org.common.exception.custom.DataNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;



public class SecurityUtil {

    private SecurityUtil() {}

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            throw new DataNotFoundException(ErrorMessages.UNAUTHORIZED);
        }

        // Principal을 UserPrincipal로 캐스팅해서 반환
        return (UserPrincipal) authentication.getPrincipal();
    }

    public static String getCurrentUserId() {
        return getCurrentUser().userId();
    }

    public static String getCurrentUserNickname() {
        return getCurrentUser().nickname();
    }
}
