package org.user.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.user.domain.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByNickname(String nickname);

}
