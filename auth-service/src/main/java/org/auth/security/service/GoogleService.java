package org.auth.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.auth.security.dto.response.GoogleTokenResponse;
import org.auth.security.dto.response.GoogleUserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;


@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleService {

    @Value("${google.client_id}")
    private String clientId;

    @Value("${google.client_secret}")
    private String clientSecret;

    @Value("${google.redirect_uri}")
    private String redirectUri;

    @Value("${google.token_uri}")
    private String tokenUri;

    @Value("${google.userInfo_uri}")
    private String userInfoUri;

    private final RestClient restClient;

    /**
     * 1. 구글로부터 엑세스 토큰 수령
     */
    public GoogleTokenResponse getAccessToken(final String code){
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        // 구글 서버에 토큰 요청
        return restClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body) // 요청 본문 설정
                .retrieve() // 응답을 받음
                .body(GoogleTokenResponse.class); // 응답 본문을 GoogleToken Mono 객체로 변환
    }
    /**
     * 액세스 토큰을 이용해 사용자 정보를 받아오는 메서드
     */
    public GoogleUserResponse getUserInfo(final String accessToken) {
        // WebClient를 사용해 GET 요청 전송
        return restClient.get()
                .uri(userInfoUri)
                .headers(headers -> headers.setBearerAuth(accessToken)) // 헤더에 토큰 추가
                .retrieve()
                .body(GoogleUserResponse.class);

    }
}
