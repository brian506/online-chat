package org.board.domain.dto.response;

import org.board.domain.entity.Board;
import org.board.domain.entity.Tags;

import java.time.LocalDateTime;
import java.util.List;

public record BoardResponse(String boardId, List<CommentResponse> comments, List<Tags> tags, String writerId, String writerNickname, String title, int commentCount, int viewCount, LocalDateTime createdAt) {
    public static BoardResponse from(Board board, List<CommentResponse> comments){
        return new BoardResponse(
                board.getId(),
                comments,
                board.getTags(),
                board.getWriterId(),
                board.getWriterNickname(),
                board.getTitle(),
                board.getCommentCount(),
                board.getViewCount(),
                board.getCreatedAt()
        );
    }


}
