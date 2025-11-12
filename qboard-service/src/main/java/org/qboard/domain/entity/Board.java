package org.qboard.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "board")
public class Board extends BaseTime{

    @Id @GeneratedValue
    @Column(name = "board_id",nullable = false,updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "board_topic",nullable = false)
    private BoardTopic boardTopic;

    @Column(name = "nickname",nullable = false)
    private String nickname;

    @Column(name = "tags",nullable = false)
    private String tags;

    @Column(name = "title",nullable = false)
    private String title;

    @Column(name = "content",nullable = false)
    private String content;

    @Column(name = "answer_count")
    private int answerCount; // 답변 갯수

    @Column(name = "view_count")
    private int viewCount;  // 조회수

}
