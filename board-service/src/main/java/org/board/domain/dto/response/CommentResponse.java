package org.board.domain.dto.response;

import org.board.domain.entity.Comment;

public record CommentResponse(String id, String boardId, String writerId, String writerNickname, String comment) {
    public static CommentResponse toDto(Comment comment){
        return new CommentResponse(
                comment.getId(),
                comment.getBoard().getId(),
                comment.getWriterId(),
                comment.getWriterNickname(),
                comment.getComment()
        );
    }
}
