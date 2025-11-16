package org.board.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.board.domain.dto.request.CreateAnswerRequest;
import org.board.domain.dto.response.AnswerChatResponse;
import org.board.domain.dto.response.AnswerResponse;
import org.board.domain.dto.response.UserResponse;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "answer_board")
public class Answer extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "answer_id", nullable = false, updatable = false,columnDefinition = "VARCHAR(36")
    private String id;

    @Column(name = "answerer_id",nullable = false,updatable = false)
    private String answererId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "answerer_nickname", nullable = false)
    private String answererNickname;

    @Column(name = "answerer_tags")
    private String answererTags;

    @Column(name = "answerer_jobs")
    private String answererJobs;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "room_id")
    private String roomId;

    public static Answer toAnswerEntity(CreateAnswerRequest request, UserResponse userResponse){
        return Answer.builder()
                .answererId(userResponse.userId())
                .answererNickname(userResponse.nickname())
                .answererTags(userResponse.tags())
                .answererJobs(userResponse.job())
                .comment(request.comment())
                .build();
    }

    public static AnswerResponse toAnswerDto(Answer answer){
        return new AnswerResponse(
                answer.getId(),
                answer.getQuestion().getId(),
                answer.getAnswererId(),
                answer.getAnswererNickname(),
                answer.getAnswererTags(),
                answer.getAnswererJobs(),
                answer.getComment(),
                answer.getCreatedAt()
        );
    }

    public static AnswerChatResponse toChatRoomDto(Answer answer){
        return new AnswerChatResponse(
                answer.getId(),
                answer.question.getId(),
                answer.question.getAskerId(),
                answer.getAnswererId(),
                answer.answererNickname,
                answer.question.getAskerNickname(),
                answer.getCreatedAt()
        );
    }
}
