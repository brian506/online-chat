package org.board.domain.repository;

import org.board.domain.entity.Board;
import org.board.domain.repository.custom.QuestionRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Board, String>, QuestionRepositoryCustom {
    Optional<Board> findByTitle(String title);
}
