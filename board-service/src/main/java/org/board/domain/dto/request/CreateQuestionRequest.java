package org.board.domain.dto.request;

import org.board.domain.entity.BoardTopic;

public record CreateQuestionRequest(String title, String content, BoardTopic boardTopic) {
}
