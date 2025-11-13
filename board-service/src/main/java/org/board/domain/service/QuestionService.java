package org.board.domain.service;

import lombok.RequiredArgsConstructor;
import org.board.domain.dto.request.CreateQuestionRequest;
import org.board.domain.dto.response.QuestionResponse;
import org.board.domain.dto.response.UserResponse;
import org.board.domain.entity.Question;
import org.board.domain.repository.QuestionRepository;
import org.common.utils.ErrorMessages;
import org.common.utils.OptionalUtil;
import org.common.utils.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserServiceClient userServiceClient;

    // 질문 게시글 작성
    @Transactional
    public UUID postBoard(final CreateQuestionRequest createQuestionRequest){
        String userId = SecurityUtil.getCurrentUserId();
        UserResponse userResponse = userServiceClient.getUser(userId);

        Question question = Question.toQuestionEntity(createQuestionRequest,userResponse);
        questionRepository.save(question);
        return question.getId();
    }

    // 모든 질문들 불러오기 - cursor
//    @Transactional(readOnly = true)
//    public List<QuestionResponse> getQuestionsByCursor(){
//
//    }

    // 주제별 질문들 조회

    // 특정 게시글 조회
    public QuestionResponse findQuestion(final UUID questionId){
        Question question = OptionalUtil.getOrElseThrow(questionRepository.findById(questionId), ErrorMessages.POST_NOT_FOUND);
        question.increaseViewCount();
        return Question.toQuestionDto(question);
    }

//    // 제목 조회 - 연관검색어?
//    public QuestionResponse findQuestionByTitle(final String title){
//        Question question = OptionalUtil.getOrElseThrow(questionRepository.findByTitle(title),ErrorMessages.POST_NOT_FOUND);
//
//    }

    // 조회수 카운트

    // 답변수 카운트
}
