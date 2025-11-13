package org.board.domain.dto.request;

import java.util.UUID;

public record CreateAnswerRequest(UUID questionId, String comment) {
}
