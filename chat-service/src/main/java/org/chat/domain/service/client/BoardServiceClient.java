package org.chat.domain.service.client;


import org.chat.domain.dto.response.AnswerFromBoardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "board-service")
public interface BoardServiceClient {

    @GetMapping("/v1/api/answers/chat-rooms/{answerId}")
    AnswerFromBoardResponse getBoardInfo(@PathVariable String answerId);
}
