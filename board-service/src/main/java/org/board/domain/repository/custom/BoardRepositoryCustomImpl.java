package org.board.domain.repository.custom;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.board.domain.dto.response.BoardResponse;
import org.board.domain.dto.response.CommentResponse;
import org.board.domain.entity.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Repository
@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QBoard board = QBoard.board;
    private final QUserWhiskyFavorites favorites = QUserWhiskyFavorites.userWhiskyFavorites;
    private final QComment comment = QComment.comment1;
    private final QUserBoardFollow boardFollow = QUserBoardFollow.userBoardFollow;

    /**
     * 질문 게시글을 WHERE,ORDER BY 동적 조건 생성
     * 즐겨찾기 추가한 위스키 피드
     */
    @Override
    public Slice<BoardResponse> getFavoritesBoardsByCursor(String userId, SortType sortType, Integer cursorValue, LocalDateTime cursorCreatedAt, Pageable pageable) {
        // 게시글 먼저 조회
        List<Board> boards = queryFactory
                .selectFrom(board)
                .join(favorites)
                .on(
                        favorites.whiskyId.eq(board.whiskyId)
                                .and(favorites.userId.eq(userId))
                )
                .where(
                        ltCursor(sortType, cursorValue, cursorCreatedAt)
                )
                .orderBy(getOrderSpecifiers(sortType))
                .limit(pageable.getPageSize() + 1) // 다음 페이지의 게시물이 있는지 확인하기 위함
                .fetch();
        return createSliceWithComments(boards,pageable);
    }

    /**
     * 팔로잉한 사람들의 위스키 게시물 조회
     */
    @Override
    public Slice<BoardResponse> getFollowingBoardsByCursor(String userId, SortType sortType, Integer cursorValue, LocalDateTime cursorCreatedAt, Pageable pageable) {
        List<Board> boards = queryFactory
                .selectFrom(board)
                .join(boardFollow)
                .on(boardFollow.followingId.eq(board.writerId) // 팔로잉한 사람(followee)이 쓴 글
                        .and(boardFollow.followerId.eq(userId))) // 내(follower)가 팔로우한 내역
                .where(ltCursor(sortType, cursorValue, cursorCreatedAt))
                .orderBy(getOrderSpecifiers(sortType))
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return createSliceWithComments(boards, pageable);
    }

    private Slice<BoardResponse> createSliceWithComments(List<Board> boards,Pageable pageable){
        // 게시글들의 Id 추출
        List<String> boardIds = boards.stream()
                .map(Board::getId)
                .toList();

        // IN 절로 댓글 한번에 조회
        List<CommentResponse> commentResponses = queryFactory
                .select(Projections.constructor(CommentResponse.class,
                        comment.id,
                        comment.board.id,
                        comment.writerId,
                        comment.writerNickname,
                        comment.comment,
                        comment.createdAt
                ))
                .from(comment)
                .where(comment.board.id.in(boardIds))
                .orderBy(comment.createdAt.asc()) // 오래된순
                .fetch();

        Map<String, List<CommentResponse>> commentMap = commentResponses.stream()
                .collect(Collectors.groupingBy(CommentResponse::boardId));

        // entity -> dto
        List<BoardResponse> finalResponses = boards.stream()
                .map(b -> BoardResponse.from(
                        b,
                        commentMap.getOrDefault(b.getId(), Collections.emptyList())
                ))
                .toList();

        return checkHasNext(finalResponses, pageable);
    }


    // 정렬 타입에 따른 WHERE 절 결정
    private BooleanExpression ltCursor(SortType sortType, Integer cursorValue, LocalDateTime cursorCreatedAt) {

        // 첫 페이지 (커서 없음)
        if (cursorValue == null && cursorCreatedAt == null) {
            return null;
        }

        // 커서 존재
        switch (sortType) {
            // 인기순 - 답변 순(답변 수, 생성일자 내림차순), 좋아요 순?
            case POPULAR:
                if (cursorValue == null || cursorCreatedAt == null) return null;
                return board.commentCount.lt(cursorValue) // 1. 첫번쨰 조건 : commentCount < cursorValue - 답변수가 cursorValue 보다 작은 값들을 더 불러오라
                        .or(board.commentCount.eq(cursorValue) // 2. 두번째 조건 : commentCount = cursorValue 인 답변수
                                .and(board.createdAt.lt(cursorCreatedAt))); // 3. 세번째 조건 : 내림차순
            // 조회순 - (조회수,생성일자 내림차순)
            case VIEWS:
                if (cursorValue == null || cursorCreatedAt == null) return null;
                return board.viewCount.lt(cursorValue)
                        .or(board.viewCount.eq(cursorValue)
                                .and(board.createdAt.lt(cursorCreatedAt)));
            // 오래된 순
            case OLD:
                if (cursorCreatedAt == null) return null;
                else return board.createdAt.gt(cursorCreatedAt);
                // 최신순 - 기본값
            case LATEST:
            default:
                if (cursorCreatedAt == null) return null;
                else return board.createdAt.lt(cursorCreatedAt);
        }
    }

    // ORDER BY 절
    private OrderSpecifier<?>[] getOrderSpecifiers(SortType sortType) {
        switch (sortType) {
            case POPULAR: //todo 조회순,생성일자에 대한 복합인덱스 생성할지?
                return new OrderSpecifier[]{board.commentCount.desc(), board.createdAt.desc()};
            case VIEWS:
                return new OrderSpecifier[]{board.viewCount.desc(), board.createdAt.desc()};
            case OLD:
                return new OrderSpecifier[]{board.createdAt.asc()};
            case LATEST:
            default:
                return new OrderSpecifier[]{board.createdAt.desc()};
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
