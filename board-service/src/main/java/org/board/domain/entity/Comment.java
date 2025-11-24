package org.board.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.board.domain.dto.request.CreateCommentRequest;
import org.board.domain.dto.response.UserResponse;
import org.common.utils.UserPrincipal;
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

    public static Comment toCommentEntity(CreateCommentRequest request, UserPrincipal loginUser){
        return Comment.builder()
                .writerId(loginUser.userId())
                .writerNickname(loginUser.nickname())
                .comment(request.comment())
                .build();
    }


}
