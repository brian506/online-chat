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

    // todo 페이징 조회 ID 커버링 인덱스로 최적화
    private final JPAQueryFactory queryFactory;
    private final QBoard board = QBoard.board;
    private final QUserWhiskyFavorites favorites = QUserWhiskyFavorites.userWhiskyFavorites;
    private final QComment comment = QComment.comment1;
    private final QBoardUserFollow boardFollow = QBoardUserFollow.boardUserFollow;

    /**
     * 질문 게시글을 WHERE,ORDER BY 동적 조건 생성
     * 즐겨찾기 추가한 위스키 피드
     */
    @Override
    public Slice<BoardResponse> getFavoritesBoardsByCursor(
            String userId,
            SortType sortType,
            Integer cursorValue,
            LocalDateTime cursorCreatedAt,
            String cursorId,
            Pageable pageable
    ) {
        List<Board> boards = queryFactory
                .selectFrom(board)
                .join(favorites)
                .on(favorites.whiskyId.eq(board.whiskyId)
                        .and(favorites.userId.eq(userId)))
                .where(
                        ltCursor(sortType, cursorValue, cursorCreatedAt, cursorId) // ★ 수정됨
                )
                .orderBy(getOrderSpecifiers(sortType))
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return createSliceWithComments(boards, pageable);
    }

    /**
     * 팔로잉한 사람들의 위스키 게시물 조회
     */
    @Override
    public Slice<BoardResponse> getFollowingBoardsByCursor(
            String userId,
            SortType sortType,
            Integer cursorValue,
            LocalDateTime cursorCreatedAt,
            String cursorId,
            Pageable pageable
    ) {
        List<Board> boards = queryFactory
                .selectFrom(board)
                .join(boardFollow)
                .on(boardFollow.followingId.eq(board.writerId)
                        .and(boardFollow.followerId.eq(userId)))
                .where(
                        ltCursor(sortType, cursorValue, cursorCreatedAt, cursorId)
                )
                .orderBy(getOrderSpecifiers(sortType))
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return createSliceWithComments(boards, pageable);
    }

    /**
     * board 에 맞는 comment Slice 처리
     */
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
    private BooleanExpression ltCursor(SortType sortType, Integer cursorValue, LocalDateTime cursorCreatedAt, String cursorId) {

        // 첫 페이지(커서 없음)
        if (cursorCreatedAt == null || cursorId == null) {
            return null;
        }

        switch (sortType) {
            case POPULAR: // 댓글수(desc) -> 시간(desc) -> ID(asc)
                return board.commentCount.lt(cursorValue)
                        .or(board.commentCount.eq(cursorValue)
                                .and(board.createdAt.lt(cursorCreatedAt)))
                        .or(board.commentCount.eq(cursorValue)
                                .and(board.createdAt.eq(cursorCreatedAt))
                                .and(board.id.gt(cursorId))); // ID는 ASC 정렬이므로 GT(큰값)가 다음 페이지

            case VIEWS: // 조회수(desc) -> 시간(desc) -> ID(asc)
                return board.viewCount.lt(cursorValue)
                        .or(board.viewCount.eq(cursorValue)
                                .and(board.createdAt.lt(cursorCreatedAt)))
                        .or(board.viewCount.eq(cursorValue)
                                .and(board.createdAt.eq(cursorCreatedAt))
                                .and(board.id.gt(cursorId)));

            case OLD: // 시간(asc) -> ID(asc) :: 과거 -> 미래
                return board.createdAt.gt(cursorCreatedAt) // 더 미래의 시간을 가져옴
                        .or(board.createdAt.eq(cursorCreatedAt)
                                .and(board.id.gt(cursorId)));

            case LATEST: // 시간(desc) -> ID(asc) :: 미래 -> 과거
            default:
                return board.createdAt.lt(cursorCreatedAt) // 더 과거의 시간을 가져옴
                        .or(board.createdAt.eq(cursorCreatedAt)
                                .and(board.id.gt(cursorId)));
        }
    }

    // ORDER BY 절
    private OrderSpecifier<?>[] getOrderSpecifiers(SortType sortType) {
        switch (sortType) {
            case POPULAR:
                return new OrderSpecifier[]{
                        board.commentCount.desc(),
                        board.createdAt.desc(),
                        board.id.asc() // ★ 필수 추가
                };
            case VIEWS:
                return new OrderSpecifier[]{
                        board.viewCount.desc(),
                        board.createdAt.desc(),
                        board.id.asc() // ★ 필수 추가
                };
            case OLD:
                return new OrderSpecifier[]{
                        board.createdAt.asc(),
                        board.id.asc() // ★ 필수 추가
                };
            case LATEST:
            default:
                return new OrderSpecifier[]{
                        board.createdAt.desc(),
                        board.id.asc() // ★ 필수 추가
                };
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
