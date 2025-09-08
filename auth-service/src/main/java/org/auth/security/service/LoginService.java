package org.auth.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.auth.domain.entity.Role;
import org.auth.domain.entity.Token;
import org.auth.domain.entity.User;
import org.auth.domain.repository.TokenRepository;
import org.auth.domain.repository.UserRepository;
import org.auth.security.dto.response.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final GoogleService googleService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;


    @Transactional
    public LoginResponse login(final String code) {

        GoogleTokenResponse tokenResponse = googleService.getAccessToken(code);
        String googleToken = tokenResponse.accessToken();

        GoogleUserResponse userResponse = googleService.getUserInfo(googleToken);
        User user = userRepository.findByEmail(userResponse.email()).
                orElseGet(() -> saveUser(userResponse));
        log.info("저장된 사용자 이메일 : " + user.getEmail());

        String accessToken = jwtService.createAccessToken(new AccessTokenPayload(user.getEmail(), Role.GENERAL, new Date()));
        String refreshToken = jwtService.createRefreshToken(new RefreshTokenPayload(user.getEmail(), new Date()));
        user.setAccessToken(accessToken);

        // refreshToken 레디스에 저장
        Token token = new Token(user.getEmail(),refreshToken,Role.GENERAL);
        tokenRepository.save(token);

        log.info("저장된 리프레시토큰 : " + token.getRefreshToken());

        return new LoginResponse(Role.GENERAL, accessToken, refreshToken);
    }

    private User saveUser(GoogleUserResponse response) {
        User user = User.builder()
                .email(response.email())
                .role(Role.GENERAL)
                .name(response.name())
                .build();
        return userRepository.save(user);
    }
}
