package org.board.domain.repository;

import org.board.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, String> {
    //todo 여기서 boardId 로 찾아서 게시물 페이징조회
}
