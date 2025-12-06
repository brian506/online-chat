//package org.auth.unit_test;
//
//import org.assertj.core.api.Assertions;
//import org.auth.domain.dto.request.LoginRequest;
//import org.auth.domain.dto.response.AccessTokenPayload;
//import org.auth.domain.dto.response.LoginResponse;
//import org.auth.domain.dto.response.RefreshTokenPayload;
//import org.auth.domain.entity.AuthUser;
//import org.auth.domain.entity.Role;
//import org.auth.domain.entity.RefreshToken;
//import org.auth.domain.repository.AuthUserRepository;
//import org.auth.domain.repository.redis.TokenRepository;
//import org.auth.security.service.JwtService;
//import org.auth.domain.service.LoginService;
//import org.common.exception.custom.AuthenticationException;
//import org.common.utils.ErrorMessages;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.BDDMockito;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Date;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//public class LoginServiceTest {
//    @InjectMocks
//    private LoginService loginService;
//
//    @Mock
//    private AuthUserRepository authUserRepository;
//
//    @Mock
//    private JwtService jwtService;
//
//    @Mock
//    private TokenRepository tokenRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Test
//    @DisplayName("로그인 성공 테스트")
//    void login_SUCCESS() throws Exception {
//        //given
//        LoginRequest loginRequest = new LoginRequest("test@test.com","password");
//        // 로그인은 값이 맞냐 틀리냐만 중요하므로 비밀번호를 encode() 하지 않고 그냥 true 로 반환
//        given(passwordEncoder.matches(anyString(),anyString())).willReturn(true);
//
//        AuthUser authUser = AuthUser.builder().email(loginRequest.email()).password("encodePassword").build();
//        given(authUserRepository.findByEmail(loginRequest.email())).willReturn(Optional.of(authUser));
//        // 토큰의 발급을 테스트 하는 것이므로 jwtService를 given() 에서 호출
//        given(jwtService.createAccessToken(any())).willReturn("accessToken");
//        given(jwtService.createRefreshToken(any())).willReturn("refreshToken");
//        // 첫 로그인이므로 "refreshToken"으로 조회하면 없다고 가정
//        given(tokenRepository.findByRefreshToken("refreshToken")).willReturn(Optional.empty());
//        //when
//        LoginResponse loginResponse = loginService.login(loginRequest);
//        //then
//        Assertions.assertThat(loginResponse.accessToken()).isEqualTo("accessToken");
//        Assertions.assertThat(loginResponse.refreshToken()).isEqualTo("refreshToken");
//
//        // 토큰리포지토리가 한번이라도 호출됐는지 검증
//        verify(tokenRepository,times(1)).save(any(RefreshToken.class));
//
//    }
//    @Test
//    @DisplayName("비밀번호 불일치로 로그인 실패 테스트")
//    void invalidPasswordLogin_FAIL() throws Exception {
//        //given
//        LoginRequest loginRequest = new LoginRequest("test@test.com","password");
//        AuthUser user = AuthUser.builder()
//                .email(loginRequest.email())
//                .password(loginRequest.password())
//                .build();
//
//        given(authUserRepository.findByEmail(anyString())).willReturn(Optional.of(user));
//        // 비밀번호 틀렸다고 설정
//        given(passwordEncoder.matches(anyString(),anyString())).willReturn(false);
//        //when
//
//        //then
//        assertThatThrownBy(()-> loginService.login(loginRequest))
//                .isInstanceOf(RuntimeException.class);
//    }
//}
