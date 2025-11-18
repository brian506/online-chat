package org.board.domain.dto.response;

import org.board.domain.entity.Tags;

import java.time.LocalDateTime;
import java.util.List;

public record BoardResponse(String questionId, List<Tags> tags, String nickname, String jobs, String title, int answerCount, int viewCount, LocalDateTime createdAt) {

}
