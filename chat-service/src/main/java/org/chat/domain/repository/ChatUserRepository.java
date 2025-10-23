package org.chat.domain.repository;

import org.chat.domain.entity.ChatUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatUserRepository extends MongoRepository<ChatUser,String> {

    boolean existsByNickname(String nickname);
    Optional<ChatUser> findByUsername(String username);
}
