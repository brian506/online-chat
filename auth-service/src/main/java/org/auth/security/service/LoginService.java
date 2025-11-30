package org.auth.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.auth.domain.dto.request.LoginRequest;
import org.auth.domain.entity.Role;
import org.auth.domain.entity.Token;
import org.auth.domain.entity.AuthUser;
import org.auth.domain.repository.redis.TokenRepository;
import org.auth.domain.repository.AuthUserRepository;
import org.auth.domain.dto.response.AccessTokenPayload;
import org.auth.domain.dto.response.LoginResponse;
import org.auth.domain.dto.response.RefreshTokenPayload;
import org.common.exception.custom.AuthenticationException;
import org.common.redis.FcmToken;
import org.common.redis.FcmTokenRepository;
import org.common.utils.ErrorMessages;
import org.common.utils.OptionalUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthUserRepository authUserRepository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final FcmTokenRepository fcmTokenRepository;


    @Transactional
    public LoginResponse login(final LoginRequest loginRequest) {

        AuthUser user = OptionalUtil.getOrElseThrow(authUserRepository.findByEmail(loginRequest.email()), ErrorMessages.USER_NOT_FOUND);

        validatePassword(loginRequest,user);
        // Fcm 토큰 db 에 저장
        if(loginRequest.fcmToken() == null) {
            fcmTokenRepository.save(new FcmToken(user.getId(), loginRequest.fcmToken()));
        }

        String refreshToken = jwtService.createRefreshToken(new RefreshTokenPayload(user.getId(), new Date()));
        updateRefreshToken(user,refreshToken);

        String accessToken = jwtService.createAccessToken(new AccessTokenPayload(user.getId(), user.getNickname(),Role.GENERAL, new Date()));
        return new LoginResponse(Role.GENERAL, accessToken, refreshToken);
    }

    private void updateRefreshToken(AuthUser user,String token){
        // refreshToken 레디스에 저장, 쿠키로 변환은 컨트롤러에서
        tokenRepository.findByRefreshToken(token).ifPresent(tokenRepository::delete);
        tokenRepository.save(Token.toEntity(user, token));
    }

    private void validatePassword(LoginRequest request,AuthUser user){
        // 로그인할때도 암호화 과정으로 비교검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthenticationException(ErrorMessages.INVALID_CREDENTIALS);
        }
    }
}
