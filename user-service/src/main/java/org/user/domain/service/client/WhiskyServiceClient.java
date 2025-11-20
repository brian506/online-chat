package org.user.domain.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.user.domain.dto.response.WhiskyFavoritesResponse;

@FeignClient(name = "whisky-service")
public interface WhiskyServiceClient {

    @GetMapping("/v1/api/whisky/{whiskyId}")
    WhiskyFavoritesResponse getUserFavorites(@PathVariable String whiskyId);
}
