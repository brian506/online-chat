package org.user.domain.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.user.domain.dto.request.AuthRegisterRequest;
import org.user.domain.dto.response.AuthRegisterResponse;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @PostMapping("/v1/api/auth/sign-up")
    AuthRegisterResponse registerUser(@RequestBody AuthRegisterRequest authRegisterRequest);
}
