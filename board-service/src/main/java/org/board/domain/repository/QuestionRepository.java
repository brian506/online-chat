package org.board.domain.repository;

import org.board.domain.entity.Question;
import org.board.domain.repository.custom.QuestionRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, String>, QuestionRepositoryCustom {
    Optional<Question> findByTitle(String title);
}
