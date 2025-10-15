package org.chat.domain.dto;


import org.chat.domain.entity.UserType;

public record Sender (String userId, String nickname, String username, UserType type){ }
