package org.board.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.board.domain.dto.request.CreateBoardRequest;
import org.board.domain.dto.response.UserResponse;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "board", indexes = {  // 주제,생성일 복합인덱스
        @Index(name = "idx_topic_created_at_desc", columnList = "whiskyId, createdAt DESC")
})
public class Board extends BaseTime{

    //todo boardtopic,createdAt,id 조건으로 복합인덱스

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "question_id", nullable = false, updatable = false)
    private String id;

    @Column(name = "whisky_id", nullable = false)
    private String whiskyId;

    // 조회할 때는 post.get() 으로 안하고 CommentRepository 에서 조회 - 양방향 연관관계이지만 단방향 성질 이용
    // 게시물이 삭제될 때만 댓글도 같이 삭제 되게 하기 위해
    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @Column(name = "writer_id",nullable = false,updatable = false)
    private String writerId;

    @Column(name = "writer_nickname", nullable = false)
    private String writerNickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "tags", nullable = false)
    private List<Tags> tags;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Setter
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "comment_count")
    private int commentCount; // 답변 갯수

    @Column(name = "view_count")
    private int viewCount;  // 조회수

    public void increaseCommentCount(){
        this.commentCount++;
    }

    public void increaseViewCount(){
        this.viewCount++;
    }

    public static Board toBoardEntity(CreateBoardRequest request, UserResponse userResponse){
        return Board.builder()
                .writerId(userResponse.userId())
                .whiskyId(request.whiskyId())
                .writerNickname(userResponse.nickname())
                .tags(request.tags())
                .title(request.title())
                .content(request.content())
                .build();
    }

}
