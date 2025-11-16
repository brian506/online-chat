package org.board.domain.repository.custom;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.board.domain.dto.response.QuestionResponse;
import org.board.domain.entity.BoardTopic;
import org.board.domain.entity.QQuestion;
import org.board.domain.entity.SortType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
@RequiredArgsConstructor
public class QuestionRepositoryCustomImpl implements QuestionRepositoryCustom{

    private JPAQueryFactory queryFactory;
    private QQuestion question = QQuestion.question;

    /**
     * 질문 게시글을 WHERE,ORDER BY 동적 조건 생성
     */
    @Override
    public Slice<QuestionResponse> getQuestionsByCursor(BoardTopic boardTopic, SortType sortType, Integer cursorValue, LocalDateTime cursorCreatedAt, Pageable pageable) {
        List<QuestionResponse> responses = queryFactory
                .select(Projections.constructor(QuestionResponse.class,
                        question.id,
                        question.boardTopic,
                        question.askerNickname,
                        question.title,
                        question.answerCount,
                        question.viewCount,
                        question.createdAt
                ))
                .from(question)
                .where(
                        question.boardTopic.eq(boardTopic),
                        ltCursor(sortType, cursorValue, cursorCreatedAt)
                )
                .orderBy(getOrderSpecifiers(sortType))
                .limit(pageable.getPageSize() + 1) // 다음 페이지의 게시물이 있는지 확인하기 위함
                .fetch();
        return checkHasNext(responses,pageable);

    }

    // 정렬 타입에 따른 WHERE 절 결정
    private BooleanExpression ltCursor(SortType sortType, Integer cursorValue,LocalDateTime cursorCreatedAt){

        // 첫 페이지 (커서 없음)
        if(cursorValue == null && cursorCreatedAt == null){
            return null;
        }

        // 커서 존재
        switch (sortType){
            // 인기순 - 답변 순(답변 수, 생성일자 내림차순)
            case POPULAR:
                if (cursorValue == null || cursorCreatedAt == null) return null;
                return question.answerCount.lt(cursorValue) // 1. 첫번쨰 조건 : answerCount < cursorValue - 답변수가 cursorValue 보다 작은 값들을 더 불러오라
                        .or(question.answerCount.eq(cursorValue) // 2. 두번째 조건 : answerCount = cursorValue 인 답변수
                                .and(question.createdAt.lt(cursorCreatedAt))); // 3. 세번째 조건 : 내림차순
            // 조회순 - (조회수,생성일자 내림차순)
            case VIEWS:
                if(cursorValue == null || cursorCreatedAt == null) return null;
                return question.viewCount.lt(cursorValue)
                        .or(question.viewCount.eq(cursorValue)
                                .and(question.createdAt.lt(cursorCreatedAt)));
            // 오래된 순
            case OLD:
                if(cursorCreatedAt == null) return null;
                else return question.createdAt.gt(cursorCreatedAt);
            // 최신순 - 기본값
            case LATEST:
            default:
                if(cursorCreatedAt == null) return null;
                else return question.createdAt.lt(cursorCreatedAt);
        }
    }
    // ORDER BY 절
    private OrderSpecifier<?>[] getOrderSpecifiers(SortType sortType){
        switch (sortType){
            case POPULAR: //todo 조회순,생성일자에 대한 복합인덱스 생성할지?
                return new OrderSpecifier[] { question.answerCount.desc(),question.createdAt.desc()};
            case VIEWS:
                return new OrderSpecifier[]{ question.viewCount.desc(),question.createdAt.desc()};
            case OLD:
                return new OrderSpecifier[]{ question.createdAt.asc()};
            case LATEST:
            default:
                return new OrderSpecifier[]{ question.createdAt.desc()};
        }
    }
    // 다음 데이터가 있는지 확인
    private <T> Slice<T> checkHasNext(List<T> content, Pageable pageable) {
        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }

}
