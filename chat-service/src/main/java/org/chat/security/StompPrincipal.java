package org.chat.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

@RequiredArgsConstructor
@Getter
public class StompPrincipal implements Principal {

    private final String userId;

    @Override
    public String getName() {
        return userId;
    }
}
/**
 * accessor.getUser 는 Principal 객체를 반환해서 Principal 객체에서 userId 값을 가져오려면
 * 이 클래스처럼 커스텀으로 userId 을 반환해주는 클래스를 만들어야함
 */
