package org.board.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.common.event.FollowEvent;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "board_user_follow",indexes = {
        @Index(name = "idx_board_user_follow",columnList = "followerId, followingId")
})
public class BoardUserFollow extends BaseTime{
    /**
     * 팔로잉한 사람들의 게시물 피드 entity
     */

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "board_user_follow_id")
    private String id;

    @Column(name = "follower_id",nullable = false)
    private String followerId; // 구독한 사람(나)

    @Column(name = "following_id",nullable = false)
    private String followingId; // 내가 팔로우하는 사람


    public static BoardUserFollow toEntity(FollowEvent event){
        return BoardUserFollow.builder()
                .followerId(event.followerId())
                .followingId(event.followingId())
                .build();
    }
}
