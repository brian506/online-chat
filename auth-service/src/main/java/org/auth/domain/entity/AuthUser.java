package org.auth.domain.entity;


import jakarta.persistence.*;
import lombok.*;

import org.auth.domain.dto.request.CreateUserRequest;
import org.auth.domain.dto.response.CreateUserResponse;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;




@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auth_user")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
public class AuthUser extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "auth_user_id", nullable = false, updatable = false)
    private String id;

    @Column(name = "email", nullable = false, updatable = false)
    private String email;

    @Column(name = "nickname",nullable = false)
    private String nickname;

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
                .nickname(request.nickname())
                .role(Role.GENERAL)
                .build();
    }

    public static CreateUserResponse toUserDto(AuthUser user){
        return new CreateUserResponse(
                user.getEmail(),
                user.getPassword(),
                user.getNickname()
        );
    }
}

