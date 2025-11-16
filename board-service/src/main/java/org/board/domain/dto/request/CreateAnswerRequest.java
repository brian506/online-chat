package org.board.domain.dto.request;

import java.util.UUID;

public record CreateAnswerRequest(String questionId, String comment) {
}
