package org.auth.api;

import lombok.RequiredArgsConstructor;
import org.auth.security.dto.response.LoginResponse;
import org.auth.security.service.CookieService;
import org.auth.security.service.LoginService;

import org.common.utils.SuccessResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2/callback/google")
public class AuthController {

    private final LoginService loginService;
    private final CookieService cookieService;

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestParam final String code){
        LoginResponse loginResponse = loginService.login(code);
        String refreshTokenCookie = cookieService.createRefreshTokenCookie(loginResponse.refreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION,loginResponse.accessToken())
                .header(HttpHeaders.SET_COOKIE,refreshTokenCookie)
                .body(new SuccessResponse(true,"로그인 성공",loginResponse.role()));
    }
}
/**
 * 새롭게 로그인할 때는 항상 새로운 엑세스,리프레시 토큰 재발급
 * 필터에서는 로그인하고 있는 도중에 엑세스 토큰이 만료되었을 때 리프레시토큰과 엑세스 토큰 재발급
 */
