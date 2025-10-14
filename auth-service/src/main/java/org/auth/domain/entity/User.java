package org.auth.domain.entity;


import jakarta.persistence.*;
import lombok.*;

import org.auth.config.BaseTime;
import org.auth.security.dto.response.GoogleUserResponse;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;


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
    private UUID id;

    @Column(name = "email", nullable = false, updatable = false)
    private String email;

    @Column(name = "username", nullable = false, updatable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Setter
    @Column(name = "user_role",nullable = false)
    private Role role;

    public static User saveUser(GoogleUserResponse response){
        return User.builder()
                .email(response.email())
                .role(Role.GENERAL)
                .username(response.name())
                .build();
    }
}

