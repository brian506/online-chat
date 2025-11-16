package org.board.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.board.domain.dto.request.CreateQuestionRequest;
import org.board.domain.dto.response.QuestionResponse;
import org.board.domain.dto.response.UserResponse;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "question_board", indexes = {  // 주제,생성일 복합인덱스
        @Index(name = "idx_topic_created_at_desc", columnList = "boardTopic, createdAt DESC")
})
public class Question extends BaseTime{

    //todo boardtopic,createdAt,id 조건으로 복합인덱스

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "question_id", nullable = false, updatable = false,columnDefinition = "VARCHAR(36")
    private String id;

    @Column(name = "asker_id",nullable = false,updatable = false)
    private String askerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "board_topic", nullable = false)
    private BoardTopic boardTopic;

    @Column(name = "asker_nickname", nullable = false)
    private String askerNickname;

    @Column(name = "asker_tags", nullable = false)
    private String askerTags;

    @Column(name = "asker_jobs",nullable = false)
    private String askerJobs;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

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

    public static Question toQuestionEntity(CreateQuestionRequest request, UserResponse userResponse){
        return Question.builder()
                .askerId(userResponse.userId())
                .boardTopic(request.boardTopic())
                .askerNickname(userResponse.nickname())
                .askerTags(userResponse.tags())
                .askerJobs(userResponse.job())
                .title(request.title())
                .content(request.content())
                .build();
    }
    public static QuestionResponse toQuestionDto(Question question){
        return new QuestionResponse(
                question.getId(),
                question.getBoardTopic(),
                question.getAskerNickname(),
                question.getAskerTags(),
                question.getAskerJobs(),
                question.getTitle(),
                question.getAnswerCount(),
                question.getViewCount(),
                question.getCreatedAt()
        );
    }
}
