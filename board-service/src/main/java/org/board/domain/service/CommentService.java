package org.board.domain.service;

import lombok.RequiredArgsConstructor;
import org.board.domain.dto.request.CreateCommentRequest;
import org.board.domain.dto.response.CommentResponse;
import org.board.domain.dto.response.UserResponse;
import org.board.domain.entity.Comment;
import org.board.domain.entity.Board;
import org.board.domain.repository.CommentRepository;
import org.board.domain.repository.BoardRepository;
import org.common.utils.ErrorMessages;
import org.common.utils.ListUtil;
import org.common.utils.OptionalUtil;
import org.common.utils.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;

    // 해당 질문에 대한 답변 게시
    @Transactional
    public CommentResponse postAnswer(final CreateCommentRequest request){
        String writerId = SecurityUtil.getCurrentUserId();
        UserResponse userResponse = userServiceClient.getUser(writerId);

        Board board = OptionalUtil.getOrElseThrow(boardRepository.findById(request.boardId()), ErrorMessages.POST_NOT_FOUND);
        board.increaseCommentCount();

        Comment comment = Comment.toCommentEntity(request,userResponse);
        commentRepository.save(comment);
        return CommentResponse.toDto(comment);
    }

    // 해당 게시물들의 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(final String boardId){
        List<Comment> comments = ListUtil.getOrElseThrowList(commentRepository.findByBoardId(boardId),ErrorMessages.COMMENT_NOT_FOUND);
        return comments.stream()
                .map(CommentResponse::toDto)
                .toList();

    }




}
