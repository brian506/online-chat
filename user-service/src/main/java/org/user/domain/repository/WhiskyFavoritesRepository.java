package org.user.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.user.domain.dto.request.WhiskyFavoritesRequest;
import org.user.domain.entity.WhiskyFavorites;

import java.util.Optional;
import java.util.UUID;

public interface WhiskyFavoritesRepository extends JpaRepository<WhiskyFavorites, UUID> {
    Optional<WhiskyFavorites> findByUserIdAndWhiskyId(String userId, String whiskyId);
}
