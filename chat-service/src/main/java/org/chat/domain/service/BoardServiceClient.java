package org.chat.domain.service;


import org.chat.domain.dto.response.AnswerFromBoardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "board-service")
public interface BoardServiceClient {

    @GetMapping("/v1/api/answers/{answerId}")
    AnswerFromBoardResponse getUser(@PathVariable UUID answerId);
}
