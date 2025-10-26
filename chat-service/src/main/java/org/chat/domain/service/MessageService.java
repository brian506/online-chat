package org.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chat.domain.dto.request.SendMessageEvent;
import org.chat.domain.dto.response.MessageBroadcastResponse;
import org.chat.domain.dto.response.MessageListResponse;
import org.chat.domain.dto.response.MessageReadResponse;
import org.chat.domain.dto.response.MessageResponse;
import org.chat.domain.entity.Message;
import org.chat.domain.repository.MessageRepository;
import org.common.utils.ListUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final PublishService publishService;

    // 메시지 전송
    public void createMessage(final SendMessageEvent message,final String senderId){
        Message messageToSave = Message.saveMessage(message, senderId);
        Message savedMessage = messageRepository.save(messageToSave);
        MessageBroadcastResponse response = Message.toBroadCastResponse(savedMessage,senderId);
        publishService.publishMessage(response);
    }

    // 특정 채팅방에서 메시지들 조회 - list 방식
    public List<MessageResponse> getMessagesFromRoom(final String roomId){
        List<Message> messages = ListUtil.getOrElseThrowList(messageRepository.findByRoomId(roomId),"존재하지 않는 채팅방입니다.");
        return messages.stream()
                .map(Message::toRoomResponse)
                .collect(Collectors.toList());
    }

    // 특정 채팅방에서 메시지들 조회 - cursor 방식
    public MessageListResponse getMessagesFromRoomCursor(final String roomId, final String cursor){
        List<Message> messages = messageRepository.getMessagesFromRoomId(roomId, cursor);
        // 다음 커서를 계산한 후에 뒤집기
        String nextCursor = messages.get(messages.size() - 1).getId();
        Collections.reverse(messages); // 가져온 데이터가 가장 최신순이기 때문에 최신순을 가장 아래에 보이게 하려면 정렬 순서를 뒤집어야함

        List<MessageResponse> messageResponses = messages.stream()
                .map(Message::toRoomResponse)
                .toList();
        return new MessageListResponse(messageResponses,nextCursor);
    }


}
