package org.user.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.user.config.BaseTime;
import org.user.domain.dto.request.CreateUserRequest;
import org.user.domain.dto.request.UserPreferenceRequest;

import java.time.LocalDate;
import java.util.List;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseTime {

    @Id
    @Column(name = "user_id",nullable = false,updatable = false)
    private String id; // auth 에서 생성한 userId로 매핑

    @Column(name = "nickname",nullable = false)
    private String nickname;

    @Column(name = "birth",nullable = false)
    private LocalDate birth;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private Level level; // 초급자,중급자,고급자

    @Column(name = "prefer_taste")
    @Enumerated(EnumType.STRING)
    private Taste taste; // 맛 선호도

    @Column(name = "grade")
    @Enumerated(EnumType.STRING)
    private Grade grade; // 서비스 내 등급

    // 회원가입
    public static User signUpDtoToEntity(CreateUserRequest request,String userId){
        return User.builder()
                .id(userId)
                .nickname(request.nickname())
                .birth(request.birth())
                .gender(request.gender())
                .grade(Grade.NEWBIE) // 첫 회원가입시 뉴비
                .build();
    }

    // 선호도 조사
    public void updatePreferences(UserPreferenceRequest request){
        this.level = request.level();
        this.taste = request.taste();
    }



}
