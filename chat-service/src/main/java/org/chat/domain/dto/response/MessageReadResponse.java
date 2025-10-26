package org.chat.domain.dto.response;

import org.chat.domain.entity.Participant;

public record MessageReadResponse(String roomId, String userId, String lastReadMessageId) {}

