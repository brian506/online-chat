package org.qboard.domain.dto.response;

import org.qboard.domain.entity.BoardTopic;

public record PostBoardResponse(Long id, BoardTopic boardTopic,String nickname,String tags,String title,String content,int answerCount,int viewCount) {
}
