package org.chat.domain.repository.customRepository;

import org.chat.domain.dto.response.RoomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomRepositoryCustom {
    Page<RoomResponse> findMyAsk(String userId, Pageable pageable);
    Page<RoomResponse> findMyAnswer(String userId, Pageable pageable);
}
