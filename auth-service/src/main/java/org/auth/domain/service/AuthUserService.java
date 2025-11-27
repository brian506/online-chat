package org.auth.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.auth.domain.dto.request.CreateUserRequest;
import org.auth.domain.dto.response.CreateUserResponse;
import org.auth.domain.entity.AuthUser;
import org.auth.domain.repository.AuthUserRepository;
import org.auth.domain.repository.redis.TokenRepository;
import org.common.exception.custom.ConflictException;
import org.common.utils.ErrorMessages;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthUserService {

    private final AuthUserRepository authUserRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public CreateUserResponse signUp(final CreateUserRequest request){
        validateEmail(request.email());

        String encodedPassword = passwordEncoder.encode(request.password());
        AuthUser authUser = AuthUser.saveUser(request,encodedPassword);

        authUserRepository.save(authUser);
        return AuthUser.toUserDto(authUser);
    }

    // 로그아웃
    public void logout(final String userId){
        tokenRepository.deleteByUserId(userId);
    }

    // 이메일 중복 검증
    private void validateEmail(final String email){
        if(authUserRepository.existsByEmail(email)){
            throw new ConflictException(ErrorMessages.DUPLICATE_EMAIL);
        }
    }
}
