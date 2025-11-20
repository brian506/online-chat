package org.whisky.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

//    @OneToOne(mappedBy = "whisky", cascade = CascadeType.ALL)
//    private WhiskyAlias alias;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Embedded
    private WhiskyMetaData metadata;
}
