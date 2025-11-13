package org.board.domain.dto.response;

import org.board.domain.entity.BoardTopic;

import java.util.UUID;

public record QuestionResponse(UUID questionId, BoardTopic boardTopic, String nickname, String tags, String jobs, String title, String content, int answerCount, int viewCount) {
}
