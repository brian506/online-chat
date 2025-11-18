package org.board.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.board.domain.dto.request.CreateBoardRequest;
import org.board.domain.dto.response.BoardResponse;
import org.board.domain.dto.response.UserResponse;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

    @Column(name = "writer_id",nullable = false,updatable = false)
    private String writerId;

    @Column(name = "whisky_id", nullable = false)
    private String whiskyId;

    @Column(name = "writer_nickname", nullable = false)
    private String writerNickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "tags", nullable = false)
    private Tags tags;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "answer_count")
    private int answerCount; // 답변 갯수

    @Column(name = "view_count")
    private int viewCount;  // 조회수

    public void increaseAnswerCount(){
        this.answerCount++;
    }

    public void increaseViewCount(){
        this.viewCount++;
    }

    public static Board toQuestionEntity(CreateBoardRequest request, UserResponse userResponse, String whiskyId){
        return Board.builder()
                .writerId(userResponse.userId())
                .whiskyId(whiskyId)
                .writerNickname(userResponse.nickname())
                .title(request.title())
                .content(request.content())
                .build();
    }

}
