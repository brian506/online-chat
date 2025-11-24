package org.whisky.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.whisky.domain.dto.WhiskyApiResponse;

@Entity
@Table(name = "whisky")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Whisky {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "whisky_id", nullable = false, updatable = false)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "nose")
    private String nose;

    @Column(name = "image_url")
    private String imageUrl;

    @Embedded
    private WhiskyMetaData metadata;

    @Column(name = "search_keyword")
    private String searchKeyword;


    public static Whisky toWhisky(WhiskyApiResponse response){
        return Whisky.builder()
                .name(response.getName())
                .nose(response.getNose())
                .imageUrl(response.getImageUrl())
                .metadata(response.getMetadata())
                .build();
    }
}
