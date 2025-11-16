package org.board.domain.dto.response;

import org.board.domain.entity.BoardTopic;

import java.time.LocalDateTime;

public record AnswerChatResponse(String answerId, String questionId, String askerId,String answererId,String answererNickname, String askerNickname,
                                 LocalDateTime createdAt) {
}
// 질뮨자,답변자 담겨있는 객체 따로 분리