package org.auth.domain.repository;

import org.auth.domain.entity.User;
import org.auth.domain.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User,UUID> , UserRepositoryCustom {
    Optional<User> findByEmail(String email);


}
