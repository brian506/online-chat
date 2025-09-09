package org.chat.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.chat.domain.dto.Participants;
import org.common.config.BaseTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Document(collection = "rooms")
public class Room extends BaseTime {

    @Id
    private String id;

    @Field(name = "name")
    private String name;

    @Field(name = "participants")
    private List<Participants> participants;

    @Field(name = "created_at")
    private LocalDateTime createdAt;
}
