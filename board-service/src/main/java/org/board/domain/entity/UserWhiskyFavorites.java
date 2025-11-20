package org.board.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import org.board.domain.dto.event.UserFavoritesEvent;

@Entity
@Table(name = "user_whisky_favorites")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
