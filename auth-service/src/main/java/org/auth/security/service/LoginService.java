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


    @Transactional
    public LoginResponse login(final LoginRequest loginRequest) {

        AuthUser user = OptionalUtil.getOrElseThrow(authUserRepository.findByEmail(loginRequest.email()), ErrorMessages.USER_NOT_FOUND);

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new AuthenticationException(ErrorMessages.INVALID_CREDENTIALS);
        }

        String refreshToken = jwtService.createRefreshToken(new RefreshTokenPayload(user.getId(), new Date()));
        updateRefreshToken(user,refreshToken);

        String accessToken = jwtService.createAccessToken(new AccessTokenPayload(user.getId(), user.getNickname(),Role.GENERAL, new Date()));
        log.info("저장된 엑세스 토큰 : " + accessToken);
        // refreshToken 레디스에 저장, 쿠키로 변환은 컨트롤러에서
        Token token = Token.toEntity(user,refreshToken);
        tokenRepository.save(token);

        return new LoginResponse(Role.GENERAL, accessToken, refreshToken);
    }

    private void updateRefreshToken(AuthUser user,String token){
        tokenRepository.findByRefreshToken(token).ifPresent(tokenRepository::delete);
        tokenRepository.save(Token.toEntity(user, token));
    }
}
