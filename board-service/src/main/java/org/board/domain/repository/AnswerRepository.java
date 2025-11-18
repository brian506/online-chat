package org.board.domain.repository;

import org.board.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Comment, String> {
}
