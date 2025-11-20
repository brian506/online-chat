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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(name = "writer_id",nullable = false)
    private String writerId;

    @Column(name = "writer_nickname", nullable = false)
    private String writerNickname;

    @Column(name = "comment", nullable = false)
    private String comment;

    public static Comment toCommentEntity(CreateCommentRequest request, UserResponse userResponse){
        return Comment.builder()
                .writerId(userResponse.userId())
                .writerNickname(userResponse.nickname())
                .comment(request.comment())
                .build();
    }


}
