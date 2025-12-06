package org.auth.domain.repository.redis;

import org.auth.domain.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken,String> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    void deleteByUserId(String userId);
}
