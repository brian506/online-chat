package org.chat.domain.dto.request;

import org.chat.domain.entity.Gender;

public record CreateChatUserRequest(String nickname,String username, Gender gender, String birth, String phoneNumber) {
}
