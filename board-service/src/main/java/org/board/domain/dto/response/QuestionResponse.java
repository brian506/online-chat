package org.board.domain.dto.response;

import org.board.domain.entity.BoardTopic;

import java.time.LocalDateTime;

public record QuestionResponse(String questionId, BoardTopic boardTopic, String nickname, String tags, String jobs, String title, int answerCount, int viewCount, LocalDateTime createdAt) {

}
