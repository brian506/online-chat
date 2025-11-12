package org.chat.domain.dto.request;

public record CreateChatUserRequest(String nickname,String username, Gender gender, String birth, String phoneNumber) {
}
