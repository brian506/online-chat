package org.board.domain.repository;

import org.board.domain.entity.UserWhiskyFavorites;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWhiskyFavoritesRepository extends JpaRepository<UserWhiskyFavorites,String> {
    void deleteByUserIdAndWhiskyId(String userId, String whiskyId);
}
