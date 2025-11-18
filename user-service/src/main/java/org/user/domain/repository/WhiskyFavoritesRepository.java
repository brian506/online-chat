package org.user.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.user.domain.entity.WhiskyFavorites;

import java.util.UUID;

public interface WhiskyFavoritesRepository extends JpaRepository<WhiskyFavorites, UUID> {
}
