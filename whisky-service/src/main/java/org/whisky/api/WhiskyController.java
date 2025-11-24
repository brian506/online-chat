package org.whisky.api;

import lombok.RequiredArgsConstructor;
import org.common.utils.SuccessMessages;
import org.common.utils.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.whisky.domain.dto.WhiskyResponse;
import org.whisky.domain.service.WhiskyService;

@RestController
@RequestMapping("/v1/api/whisky")
@RequiredArgsConstructor
public class WhiskyController {

    private final WhiskyService whiskyService;

    // 위스키 단일 조회
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> findWhisky(final String keyword){
        WhiskyResponse whiskyResponse = whiskyService.findWhisky(keyword);
        SuccessResponse response = new SuccessResponse(true, SuccessMessages.WHISKY_RETRIEVE_SUCCESS,whiskyResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
