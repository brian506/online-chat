package org.chat.domain.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record AnswerFromBoardResponse(String answerId, String questionId, String askerId,String answererId,String answererNickname, String askerNickname,
                                      LocalDateTime createdAt) {
}
/**
 * 채팅방에서 보여야 할 것들 board 에서 가져와야 할 것들
 * 현재 : 질문Id,답변Id, 답변작성자Id, 답변작성자의 닉네임
 * 질문 작성자Id, 질문작성자의 닉네임
 */
