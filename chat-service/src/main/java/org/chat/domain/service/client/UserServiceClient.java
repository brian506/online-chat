package org.chat.domain.service.client;

import org.chat.domain.dto.response.ChatUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/v1/api/users/{userId}")
    ChatUserResponse getUserInfo(String userId);
}
