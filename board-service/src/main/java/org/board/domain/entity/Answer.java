package org.board.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.board.domain.dto.request.CreateAnswerRequest;
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
public class Answer {

    @Id
    @GeneratedValue
    @Column(name = "answer_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "writer_id",nullable = false,updatable = false)
    private String writerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "tags")
    private String tags;

    @Column(name = "jobs")
    private String jobs;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "room_id")
    private String roomId;

    public static Answer toAnswerEntity(CreateAnswerRequest request, UserResponse userResponse){
        return Answer.builder()
                .writerId(userResponse.userId())
                .nickname(userResponse.nickname())
                .tags(userResponse.tags())
                .jobs(userResponse.job())
                .comment(request.comment())
                .build();
    }

    public static AnswerResponse toAnswerDto(Answer answer){
        return new AnswerResponse(
                answer.getId(),
                answer.getQuestion().getId(),
                answer.getWriterId(),
                answer.getNickname(),
                answer.getTags(),
                answer.getJobs(),
                answer.getComment()
        );
    }
}
