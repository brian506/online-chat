package org.board.domain.service;

import org.board.domain.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/v1/api/users/{userId}")
    UserResponse getUser(@PathVariable String userId);
}
