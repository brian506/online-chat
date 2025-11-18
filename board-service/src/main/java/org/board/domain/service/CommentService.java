package org.board.domain.service;

import lombok.RequiredArgsConstructor;
import org.board.domain.dto.request.CreateCommentRequest;
import org.board.domain.dto.response.UserResponse;
import org.board.domain.entity.Comment;
import org.board.domain.entity.Board;
import org.board.domain.repository.AnswerRepository;
import org.board.domain.repository.QuestionRepository;
import org.common.utils.ErrorMessages;
import org.common.utils.OptionalUtil;
import org.common.utils.SecurityUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserServiceClient userServiceClient;

    // 해당 질문에 대한 답변 게시
    public String postAnswer(final CreateCommentRequest request){
        String writerId = SecurityUtil.getCurrentUserId();
        UserResponse userResponse = userServiceClient.getUser(writerId);

        Board board = OptionalUtil.getOrElseThrow(questionRepository.findById(request.boardId()), ErrorMessages.POST_NOT_FOUND);
        board.increaseAnswerCount();

        Comment comment = Comment.toAnswerEntity(request,userResponse);
        answerRepository.save(comment);
        return comment.getId();
    }

    // 단일 답변 조회
    public AnswerResponse findAnswer(final String answerId){
        Comment comment = OptionalUtil.getOrElseThrow(answerRepository.findById(answerId),ErrorMessages.COMMENT_NOT_FOUND);
        return Comment.toAnswerDto(comment);
    }

    // 채팅방 개설할 때 필요한 데이터 조회
    public AnswerChatResponse getChatAnswerInfo(final String answerId){
        Comment comment = OptionalUtil.getOrElseThrow(answerRepository.findById(answerId),ErrorMessages.COMMENT_NOT_FOUND);
        return Comment.toChatRoomDto(comment);
    }


}
