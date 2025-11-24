package org.whisky.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.whisky.domain.entity.Whisky;

import java.util.Optional;

@Repository
public interface WhiskyRepository extends JpaRepository<Whisky,String> {
    Optional<Whisky> findBySearchKeyword(String searchKeyword);
}
