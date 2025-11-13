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
@Table(name = "question_board")
public class Question {

    @Id
    @GeneratedValue
    @Column(name = "question_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id",nullable = false,updatable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "board_topic", nullable = false)
    private BoardTopic boardTopic;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "tags", nullable = false)
    private String tags;

    @Column(name = "jobs",nullable = false)
    private String jobs;

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
                .userId(userResponse.userId())
                .boardTopic(request.boardTopic())
                .nickname(userResponse.nickname())
                .tags(userResponse.tags())
                .jobs(userResponse.job())
                .title(request.title())
                .content(request.content())
                .build();
    }
    public static QuestionResponse toQuestionDto(Question question){
        return new QuestionResponse(
                question.id,
                question.boardTopic,
                question.nickname,
                question.tags,
                question.jobs,
                question.title,
                question.content,
                question.answerCount,
                question.viewCount
        );
    }
}
