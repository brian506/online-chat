package org.chat.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.chat.domain.dto.request.CreateChatUserRequest;
import org.chat.domain.dto.response.ChatUserResponse;
import org.chat.domain.dto.response.DifferentUserResponse;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Builder
@Document(collection = "users")
public class ChatUser {
    // auth 에서 로그인 이후에 개인정보 입력 받아서 저장하는 컬렉션

    @Id
    private String userId; // 엑세스 토큰에서 추출한 email 로 식별

    @Indexed(unique = true) // 중복 방지
    @Field(name = "nickname")
    private String nickname; // 별명 (익명값)

    @Field(name = "gender")
    private Gender gender;

    @Field(name = "username")
    private String username; // 실명

    @Field(name = "birth")
    private String birth;

    @Field(name = "phone_number")
    private String phoneNumber;

    @Field(name = "created_at")
    private LocalDateTime createdAt;

    public static ChatUser toEntity(CreateChatUserRequest request,String userId){
        return ChatUser.builder()
                .userId(userId)
                .username(request.username())
                .nickname(request.nickname())
                .gender(request.gender())
                .birth(request.birth())
                .phoneNumber(request.phoneNumber())
                .createdAt(LocalDateTime.now())
                .build();
    }
    public static ChatUserResponse toDto(ChatUser user){
        return new ChatUserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getNickname(),
                user.getBirth(),
                user.getPhoneNumber()
        );
    }
    public static DifferentUserResponse toFindUserDto(ChatUser user){
        return new DifferentUserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getBirth()
        );
    }

}
