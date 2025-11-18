package org.user.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.user.config.BaseTime;

/**
 * 관심등록한 위스키 (위스키 즐겨찾기)
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "whisky_favorites")
@EntityListeners(AuditingEntityListener.class)
public class WhiskyFavorites extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "whisky_favorites_id")
    private String id; // 위스키 서비스에서 가져온 위스키의 Id

    @Column(name = "whisky_id")
    private String whiskyId;

    @Column(name = "name")
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
