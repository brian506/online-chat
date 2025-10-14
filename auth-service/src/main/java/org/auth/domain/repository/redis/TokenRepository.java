package org.auth.domain.repository.redis;

import org.auth.domain.entity.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<Token,String> {

    Optional<Token> findByEmail(String email);

    Optional<Token> findByRefreshToken(String refreshToken);
}
