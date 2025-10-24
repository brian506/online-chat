package org.auth.domain.entity;


import jakarta.persistence.*;
import lombok.*;

import org.auth.config.BaseTime;
import org.auth.security.dto.response.GoogleUserResponse;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
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

    @Enumerated(EnumType.STRING)
    @Setter
    @Column(name = "login_status",nullable = false)
    private LoginStatus status;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    public static User saveUser(GoogleUserResponse response){
        return User.builder()
                .email(response.email())
                .role(Role.GENERAL)
                .username(response.name())
                .status(LoginStatus.PENDING)
                .expiredAt(LocalDateTime.now().plusMinutes(10)) // 10분 안에 사용자 정보 입력 성공 만료 시간
                .build();
    }
}

