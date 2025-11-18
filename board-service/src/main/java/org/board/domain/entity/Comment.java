package org.board.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.board.domain.dto.request.CreateCommentRequest;
import org.board.domain.dto.response.UserResponse;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "comment")
public class Comment extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "comment_id", nullable = false, updatable = false)
    private String id;

    @Column(name = "comment_id",nullable = false,updatable = false)
    private String commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "writer_id",nullable = false)
    private String writerId;

    @Column(name = "writer_nickname", nullable = false)
    private String writerNickname;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "room_id")
    private String roomId;

    public static Comment toAnswerEntity(CreateCommentRequest request, UserResponse userResponse){
        return Comment.builder()
                .writerId(userResponse.userId())
                .writerNickname(userResponse.nickname())
                .comment(request.comment())
                .build();
    }

    public static AnswerResponse toAnswerDto(Comment comment){
        return new AnswerResponse(
                comment.getId(),
                comment.getBoard().getId(),
                comment.getAnswererId(),
                comment.getWriterNickname(),
                comment.getAnswererTags(),
                comment.getAnswererJobs(),
                comment.getComment(),
                comment.getCreatedAt()
        );
    }

    public static AnswerChatResponse toChatRoomDto(Comment comment){
        return new AnswerChatResponse(
                comment.getId(),
                comment.board.getId(),
                comment.board.getAskerId(),
                comment.getAnswererId(),
                comment.writerNickname,
                comment.board.getAskerNickname(),
                comment.getCreatedAt()
        );
    }
}
