package org.whisky.api;

import lombok.RequiredArgsConstructor;
import org.common.utils.SuccessMessages;
import org.common.utils.SuccessResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.whisky.domain.dto.WhiskyResponse;
import org.whisky.domain.service.WhiskyService;

@RestController
@RequestMapping("/v1/api/whiskies")
@RequiredArgsConstructor
public class WhiskyController {

    private final WhiskyService whiskyService;

    // 단일 조회
    @GetMapping("/{whiskyId}")
    public ResponseEntity<?> getWhiskyDetail(@PathVariable String whiskyId) {
        WhiskyResponse whiskyResponse = whiskyService.getWhisky(whiskyId);
        SuccessResponse response = new SuccessResponse(true,SuccessMessages.WHISKY_RETRIEVE_SUCCESS,whiskyResponse);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // 이름으로 조회
    @GetMapping
    public ResponseEntity<?> searchWhiskies(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        Slice<WhiskyResponse> responses = whiskyService.findWhisky(keyword, pageable);
        SuccessResponse response = new SuccessResponse(true,SuccessMessages.WHISKY_RETRIEVE_SUCCESS,responses);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}
