package org.chat.domain.repository.customRepository;

import org.chat.domain.entity.Message;

import java.util.List;

public interface MessageRepositoryCustom {
    List<Message> getMessagesFromRoomId(String roomId,String cursor);
}
