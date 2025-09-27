package org.chat.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

@RequiredArgsConstructor
@Getter
public class StompPrincipal implements Principal {

    /**
     * 토큰 안에 있는 username 을 추출해서 사용하기 위함
     * accessor.setUser() 안에는 principal 객체가 들어가야돼서
     */
    private final String username;

    @Override
    public String getName() {
        return username; // 화면에 쓸 때는 username
    }

}
