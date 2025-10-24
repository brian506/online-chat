package org.auth.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.auth.domain.entity.Role;
import org.auth.domain.entity.Token;
import org.auth.domain.entity.User;
import org.auth.domain.repository.redis.TokenRepository;
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

        String userId = String.valueOf(user.getId());

        String accessToken = jwtService.createAccessToken(new AccessTokenPayload(userId,user.getEmail(),Role.GENERAL, new Date()));
        String refreshToken = jwtService.createRefreshToken(new RefreshTokenPayload(userId, new Date()));
        log.info("저장된 엑세스 토큰 : " + accessToken);
        // refreshToken 레디스에 저장, 쿠키로 변환은 컨트롤러에서
        Token token = Token.toEntity(userId,refreshToken,Role.GENERAL);
        tokenRepository.save(token);

        return new LoginResponse(Role.GENERAL, accessToken, refreshToken);
    }

    private User saveUser(GoogleUserResponse response) {
        User user = User.saveUser(response);
        return userRepository.save(user);
    }
}
