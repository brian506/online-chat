package org.whisky.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "whisky_alias")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class WhiskyAlias {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "whisky_alias_id", nullable = false, updatable = false)
    private String id;

    // 조니워커,발베니 등 별칭 저장
    @Column(nullable = false)
    private String aliasName;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "whisky_id", nullable = false)
//    private Whisky whisky;
}
