package org.auth.domain.entity;


import jakarta.persistence.*;
import lombok.*;

import org.common.config.BaseTime;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
public class User extends BaseTime {

    @Id @GeneratedValue
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, updatable = false)
    private String email;

    @Column(name = "name", nullable = false, updatable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Setter
    private Role role;
}

