package org.whisky.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.whisky.config.BaseTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "whisky")
public class Whisky extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "whisky_id", nullable = false, updatable = false)
    private String id;

     @Column(name = "name")
    private String name;

    @Column(name = "nose",columnDefinition = "TEXT")
    private String nose;

    @Column(name = "palate",columnDefinition = "TEXT")
    private String palate;

    @Column(name = "finish",columnDefinition = "TEXT")
    private String finish;

    @Column(name = "image_url")
    private String imageUrl;

    @Embedded
    private WhiskyMetaData metadata;

    @Column(name = "search_keyword")
    private String searchKeyword;

}
