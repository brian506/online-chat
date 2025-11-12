package org.auth.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.auth.domain.dto.request.CreateUserRequest;
import org.auth.domain.dto.request.LoginRequest;
import org.auth.domain.dto.response.CreateUserResponse;
import org.auth.domain.dto.response.LoginResponse;
import org.auth.domain.service.AuthUserService;
import org.auth.security.service.CookieService;
import org.auth.security.service.LoginService;

import org.common.utils.SuccessMessages;
import org.common.utils.SuccessResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/auth")
public class AuthController {

    private final AuthUserService userService;
    private final LoginService loginService;
    private final CookieService cookieService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody final CreateUserRequest request){
        CreateUserResponse userResponse = userService.signUp(request);
        SuccessResponse response = new SuccessResponse(true, SuccessMessages.SIGNUP_SUCCESS,userResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestParam final LoginRequest loginRequest){
        LoginResponse loginResponse = loginService.login(loginRequest);
        String refreshTokenCookie = cookieService.createRefreshTokenCookie(loginResponse.refreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION,loginResponse.accessToken())
                .header(HttpHeaders.SET_COOKIE,refreshTokenCookie)
                .body(new SuccessResponse(true,"로그인 성공",loginResponse));
    }

    @PostMapping("/logout/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(@PathVariable String userId){
        userService.logout(userId);
        SuccessResponse response = new SuccessResponse(true,"사용자 로그아웃 성공",null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
/**
 * 새롭게 로그인할 때는 항상 새로운 엑세스,리프레시 토큰 재발급
 * 필터에서는 로그인하고 있는 도중에 엑세스 토큰이 만료되었을 때 리프레시토큰과 엑세스 토큰 재발급
 */
