package org.chat.domain.service;

import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import org.chat.domain.dto.request.CreateChatUserRequest;
import org.chat.domain.dto.response.ChatUserResponse;
import org.chat.domain.dto.response.DifferentUserResponse;
import org.chat.domain.entity.ChatUser;
import org.chat.domain.repository.ChatUserRepository;
import org.common.exception.custom.ConflictException;
import org.common.utils.OptionalUtil;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatUserService {
    /**
     * 채팅방에서는 사용자의 익명을 사용
     */
    private final ChatUserRepository repository;

    // 사용자 정보 입력 생성
    public String createUserInfo(final CreateChatUserRequest request,String email){

        if (!validateNickname(request.nickname())) {
            throw new ConflictException("이미 사용 중인 닉네임입니다.");
        }
        try{
            ChatUser user = ChatUser.toEntity(request,email);
            return repository.save(user).getNickname();
        }catch (DuplicateKeyException e) {
            // 동시에 같은 사용자가 닉네임을 중복처리했을 때 DB 단에서 유니크 제약 조건을 발동하기 위한 예외처리
            throw new ConflictException("이미 사용 중인 닉네입니다.");
        }
    }

    // 별명 중복확인
    public boolean validateNickname(final String nickname){
        return !repository.existsByNickname(nickname); // 사용중인 닉네임이면 false
    }

    // 내 정보 조회
    public ChatUserResponse getMyInfo(final String nickname){
        ChatUser user = OptionalUtil.getOrElseThrow(repository.findByNickname(nickname),"존재하지 않는 사용자입니다.");
        return ChatUser.toDto(user);
    }

    // 다른 사용자 실명으로 조회 - 대화방 생성 목적
    public DifferentUserResponse findUser(final String username){
        ChatUser user = OptionalUtil.getOrElseThrow(repository.findByUsername(username),"존재하지 않는 사용자입니다.");
        return ChatUser.toFindUserDto(user);
    }
}
