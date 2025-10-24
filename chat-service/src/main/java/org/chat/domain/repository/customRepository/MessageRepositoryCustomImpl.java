package org.chat.domain.repository.customRepository;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.chat.domain.entity.Message;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@RequiredArgsConstructor
public class MessageRepositoryCustomImpl implements MessageRepositoryCustom{

    private final MongoTemplate mongoTemplate;


    @Override
    public List<Message> getMessagesFromRoomId(String roomId, String cursor) {
        Query query = new Query();

        query.addCriteria(Criteria.where("room_id").is(roomId));
        if (cursor != null && !cursor.isEmpty()){
            query.addCriteria(Criteria.where("_id").lt(new ObjectId(cursor)));
        }

        query.with(Sort.by(Sort.Direction.DESC,"_id")) // 가장 최신 데이터 가져옴
                .limit(10);

        return mongoTemplate.find(query,Message.class);

    }
}
