package org.board.domain.repository;

import org.board.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, String> {

    List<Comment> findByBoardId(String boardId);
}
