package org.whisky.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class WhiskyMetaData {
    @Column(name = "type")
    private String type; // 예) 싱글몰트

    @Column(name = "country")
    private String country;

    @Column(name = "bottler")
    private String bottler; // 예) 탈리스커

    @Column(name = "age")
    private Integer age;

    @Column(name = "abv")
    private Double abv; // 도수
}
