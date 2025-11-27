package org.auth.unit_test;

import org.auth.domain.dto.request.CreateUserRequest;
import org.auth.domain.dto.response.CreateUserResponse;
import org.auth.domain.entity.AuthUser;
import org.auth.domain.repository.AuthUserRepository;
import org.auth.domain.repository.redis.TokenRepository;
import org.auth.domain.service.AuthUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthUserServiceTest {

    @InjectMocks
    private AuthUserService authUserService;

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 테스트 성공")
    void signup_SUCCESS() throws Exception {
        //given
        CreateUserRequest createUserRequest = new CreateUserRequest("brian@test.com","password","brian");
        given(authUserRepository.existsByEmail(createUserRequest.email())).willReturn(false);
        given(passwordEncoder.encode(createUserRequest.password())).willReturn("encodePassword");

        //when
        CreateUserResponse response = authUserService.signUp(createUserRequest);
        //then
        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo(createUserRequest.email());

        // 파라미터 값으로 비밀번호 인코딩 됐는지 검증
        ArgumentCaptor<AuthUser> userCaptor = ArgumentCaptor.forClass(AuthUser.class);
        verify(authUserRepository).save(userCaptor.capture());

        AuthUser savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("brian@test.com");
        assertThat(savedUser.getPassword()).isEqualTo("encodePassword");
    }

}
