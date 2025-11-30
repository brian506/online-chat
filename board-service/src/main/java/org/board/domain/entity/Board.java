package org.board.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.board.domain.dto.request.CreateBoardRequest;
import org.common.utils.UserPrincipal;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "boards",indexes = {
        //todo 운영환경에서 지워야됨

        // 최신순 정렬 ( whiskyId + createdAt 내림차순)
        @Index(name = "idx_board_whisky_created",columnList = "whisky_id, created_at DESC"),
        // 인기순 정렬(whiskyId + commentCount + createdAt 내림차순)
        @Index(name = "idx_board_whisky_comment",columnList = "whisky_id, comment_count DESC, createdAt DESC"),
        // 팔로잉 피드용(writerId, createdAt)
        @Index(name = "idx_board_writer_created", columnList = "writer_id, created_at DESC")
})
public class Board extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "board_id", nullable = false, updatable = false)
    private String id;

    @Column(name = "whisky_id", nullable = false)
    private String whiskyId;

    // 조회할 때는 board.get() 으로 안하고 CommentRepository 에서 조회 - 양방향 연관관계이지만 단방향 성질 이용
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

    @Column(name = "like_count")
    private int likeCount;

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

    public void increaseLikeCount(){
        this.likeCount++;
    }
    public void decreaseLikeCount(){
        if(this.likeCount > 0){
            this.likeCount--;
        }
    }

    public static Board toBoardEntity(CreateBoardRequest request, UserPrincipal loginUser){
        return Board.builder()
                .writerId(loginUser.userId())
                .whiskyId(request.whiskyId())
                .writerNickname(loginUser.nickname())
                .tags(request.tags())
                .title(request.title())
                .content(request.content())
                .build();
    }

}
