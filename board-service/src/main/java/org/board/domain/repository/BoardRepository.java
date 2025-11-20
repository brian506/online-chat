package org.board.domain.repository;

import org.board.domain.entity.Board;
import org.board.domain.repository.custom.BoardRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, String>, BoardRepositoryCustom {
    Optional<Board> findByTitle(String title);
}
