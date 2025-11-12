package org.auth.domain.entity;


import jakarta.persistence.*;
import lombok.*;

import org.auth.domain.dto.request.CreateUserRequest;
import org.auth.domain.dto.response.CreateUserResponse;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
public class AuthUser extends BaseTime {

    @Id @GeneratedValue
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "email", nullable = false, updatable = false)
    private String email;

    @Column(name = "password", nullable = false, updatable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Setter
    @Column(name = "user_role",nullable = false)
    private Role role;

    public void passwordEncode(String encodePassword) {
        this.password = encodePassword;
    }


    public static AuthUser saveUser(CreateUserRequest request){
        return AuthUser.builder()
                .email(request.email())
                .password(request.password())
                .role(Role.GENERAL)
                .build();
    }

    public static CreateUserResponse toUserDto(AuthUser user){
        return new CreateUserResponse(
                user.getEmail(),
                user.getPassword()
        );
    }
}

