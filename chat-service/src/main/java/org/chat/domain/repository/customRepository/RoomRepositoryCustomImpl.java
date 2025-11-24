package org.chat.domain.repository.customRepository;


import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.chat.domain.entity.Room;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class RoomRepositoryCustomImpl implements RoomRepositoryCustom{

    private final MongoTemplate mongoTemplate;


    // 사용자의 질문자/답변자 화면에 따른 채팅방 목록 조회(커서 기반)
    @Override
    public List<Room> findRoomsByUserId(String userId, String cursor) {

        Query query = new Query();
        // 2. 핵심 조건: participants 배열 내부 검색 ($elemMatch)
        Criteria userCriteria = Criteria.where("user_id").is(userId);


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

    // 사용자의 채팅방 목록에서 상대방 이름 or 닉네임 조회
    // 답변,질문할 때 상대방 정보가 다르게 닉네임,이름이 보여지게 해야함
    @Override
    public Optional<Room> findRoomByParticipantId(String userId, String peerName) {

        // 내 userId 불러들임
        Criteria myCriteria = Criteria.where("user_id").is(userId);

        Criteria peerCriteria = new Criteria().andOperator(
                Criteria.where("user_id").ne(userId), // 나는 제외 조건
                new Criteria().orOperator(
                        Criteria.where("username").is(peerName),
                        Criteria.where("nickname").is(peerName)
                )
        );
        Query query = new Query(
                new Criteria().andOperator(
                        Criteria.where("participants").elemMatch(myCriteria),
                        Criteria.where("participants").elemMatch(peerCriteria)
                )
        );
        Room room = mongoTemplate.findOne(query,Room.class);
        return Optional.of(room);
    }

    // 마지막읽은메시지Id 필드값만 프론트에서 받은 id 값으로 update
    @Override
    public boolean updateLastReadMessageId(String roomId, String userId, String messageId) {

        Query query = new Query(Criteria.where("_id").is(roomId)
                .and("participants.user_id").is(userId));

        // participants 배열 내 특정 사용자의 lastReadMessageId 필드만 업데이트
        Update update = new Update().set("participants.$.last_read_message_id", messageId);

        // 업데이트 실행
        UpdateResult result = mongoTemplate.updateFirst(query, update, Room.class);

        // 업데이트 성공 여부 반환 (변경된 문서가 1개 이상이면 성공)
        return result.getModifiedCount() > 0;
    }
}
