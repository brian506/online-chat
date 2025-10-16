package org.chat.domain.repository.customRepository;


import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.chat.domain.entity.Room;
import org.chat.domain.entity.UserType;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@RequiredArgsConstructor
public class RoomRepositoryCustomImpl implements RoomRepositoryCustom{

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Room> findRoomsByUserAndType(String userId, UserType userType, String cursor) {
        // 1. 기본 쿼리 객체 생성
        Query query = new Query();

        // 2. 핵심 조건: participants 배열 내부 검색 ($elemMatch)
        Criteria userCriteria = Criteria.where("user_id").is(userId)
                .and("user_type").is(userType);

        query.addCriteria(Criteria.where("participants").elemMatch(userCriteria));

        // 3. 커서 기반 페이징 조건 (동적)
        // cursor는 이전 페이지의 마지막 Room의 ID
        if (cursor != null && !cursor.isEmpty()) {
            query.addCriteria(Criteria.where("_id").lt(new ObjectId(cursor)));
        }

        // 4. 정렬 및 개수 제한
        query.with(Sort.by(Sort.Direction.DESC, "_id")) // 최신순 정렬이 커서 페이징의 핵심!
                .limit(10);

        // 5. 쿼리 실행
        return mongoTemplate.find(query, Room.class);
    }
}
