package org.chat.domain.dto.response;

import java.util.UUID;

public record AnswerFromBoardResponse(UUID answerId,UUID questionId,String writerId,String nickname) {
}
