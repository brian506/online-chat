package org.whisky.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class WhiskyAlias {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "whisky_alias_id", nullable = false, updatable = false,columnDefinition = "VARCHAR(36")
    private String id;

    // 조니워커,발베니 등 별칭 저장
    @Column(nullable = false)
    private String aliasName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "whisky_id", nullable = false)
    private Whisky whisky;
}
