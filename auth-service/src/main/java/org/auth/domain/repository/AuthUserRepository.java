package org.auth.domain.repository;

import org.auth.domain.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser,UUID> {
    Optional<AuthUser> findByEmail(String email);
    boolean validateEmail(String email);

}
