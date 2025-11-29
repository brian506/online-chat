package org.board.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import org.board.domain.dto.event.UserFavoritesEvent;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_whisky_favorites",indexes = {
        // userId + whiskyId
        @Index(name = "idx_whisky_favorites_userId_whiskyId",columnList = "user_id, whisky_id")

})
/**
 * 사용자가 즐겨찾기 한 위스키 목록
 */
public class UserWhiskyFavorites extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "favorite_whisky_id",nullable = false,updatable = false)
    private String id;

    @Column(name = "user_id",nullable = false,updatable = false)
    private String userId;   // 피드 주인

    @Column(name = "whisky_id",nullable = false,updatable = false)
    private String whiskyId;   // 즐겨찾기한 위스키 ID

    public static UserWhiskyFavorites toEntity(UserFavoritesEvent event){
        return UserWhiskyFavorites.builder()
                .userId(event.userId())
                .whiskyId(event.whiskyId())
                .build();
    }
}


