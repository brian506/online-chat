package org.user.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.user.config.BaseTime;
import org.user.domain.dto.request.CreateUserRequest;
import org.user.domain.dto.response.UserResponse;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseTime {

    @Id
    @Column(name = "user_id",nullable = false,updatable = false)
    private UUID id; // auth 에서 생성한 userId로 매핑

    @Column(name = "nickname",nullable = false)
    private String nickname;

    @Column(name = "birth",nullable = false)
    private String birth;

    @Column(name = "major")
    private String major;

    @Column(name = "job")
    @Enumerated(EnumType.STRING)
    private Job job;

    public static User signUpDtoToEntity(CreateUserRequest request,UUID userId){
        return User.builder()
                .id(userId)
                .nickname(request.nickname())
                .birth(request.birth())
                .major(request.major())
                .job(request.job())
                .build();
    }

    public static UserResponse userResponseToDto(User user){
        return new UserResponse(
                user.getId(),
                user.getNickname(),
                user.getBirth()
        );
    }
}
