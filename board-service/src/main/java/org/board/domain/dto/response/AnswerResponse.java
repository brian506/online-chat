package org.board.domain.dto.response;

import java.time.LocalDateTime;

public record AnswerResponse(String answerId, String questionId, String answererId, String nickname, String tags, String jobs, String comment,
                             LocalDateTime createdAt) {
}
