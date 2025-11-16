package org.whisky.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.whisky.domain.dto.WhiskyApiResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WhiskyApiService {

    @Value("${whisky-edition.url}")
    private String whiskyUrl;

    private final RestClient restClient;

    public List<WhiskyApiResponse> getWhiskyData(int limit, int offset){
        return restClient.get()
                .uri(whiskyUrl+ "?limit={limit}&offset={offset}", limit, offset)
                .retrieve()
                .body(new ParameterizedTypeReference<List<WhiskyApiResponse>>() {});
    }
}
