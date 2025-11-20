package org.whisky.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.whisky.domain.dto.WhiskyApiResponse;
import org.whisky.domain.entity.Whisky;
import org.whisky.domain.repository.WhiskyRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataLoadInitializer {

    private final WhiskyApiService apiService;
    private final WhiskyRepository repository;

    private static final int PAGE_SIZE = 100;

    public int loadWhiskyData() {
        int offset = 0;
        int total = 0;
        boolean hasMore = true;

        while (hasMore) {
            List<WhiskyApiResponse> list = apiService.getWhiskyData(PAGE_SIZE, offset);

            if (list == null || list.isEmpty()) {
                hasMore = false;
                break;
            }

            List<Whisky> entities = list.stream()
                    .map(WhiskyApiResponse::toWhisky)
                    .toList();

            repository.saveAll(entities);

            log.info("적재된 위스키 수: {}", entities.size());
            total += entities.size();

            if (list.size() < PAGE_SIZE) {
                hasMore = false;
            } else {
                offset += PAGE_SIZE;
            }
        }

        return total;
    }
}
