package org.board.domain.dto.response;

import java.util.UUID;

public record AnswerResponse(UUID answerId, UUID questionId, String writerId, String nickname, String tags, String jobs,String comment) {
}
