package org.board.domain.service;

import lombok.RequiredArgsConstructor;
import org.board.domain.dto.request.CreateAnswerRequest;
import org.board.domain.dto.response.AnswerChatResponse;
import org.board.domain.dto.response.AnswerResponse;
import org.board.domain.dto.response.UserResponse;
import org.board.domain.entity.Answer;
import org.board.domain.entity.Question;
import org.board.domain.repository.AnswerRepository;
import org.board.domain.repository.QuestionRepository;
import org.common.utils.ErrorMessages;
import org.common.utils.OptionalUtil;
import org.common.utils.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserServiceClient userServiceClient;

    // 해당 질문에 대한 답변 게시
    public String postAnswer(final CreateAnswerRequest request){
        String writerId = SecurityUtil.getCurrentUserId();
        UserResponse userResponse = userServiceClient.getUser(writerId);

        Question question = OptionalUtil.getOrElseThrow(questionRepository.findById(request.questionId()), ErrorMessages.POST_NOT_FOUND);
        question.increaseAnswerCount();

        Answer answer = Answer.toAnswerEntity(request,userResponse);
        answerRepository.save(answer);
        return answer.getId();
    }

    // 단일 답변 조회
    public AnswerResponse findAnswer(final String answerId){
        Answer answer = OptionalUtil.getOrElseThrow(answerRepository.findById(answerId),ErrorMessages.COMMENT_NOT_FOUND);
        return Answer.toAnswerDto(answer);
    }

    // 채팅방 개설할 때 필요한 데이터 조회
    public AnswerChatResponse getChatAnswerInfo(final String answerId){
        Answer answer = OptionalUtil.getOrElseThrow(answerRepository.findById(answerId),ErrorMessages.COMMENT_NOT_FOUND);
        return Answer.toChatRoomDto(answer);
    }


}
