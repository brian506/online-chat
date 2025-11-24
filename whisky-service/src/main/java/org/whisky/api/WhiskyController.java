package org.whisky.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.whisky.domain.service.WhiskyService;

@RestController
@RequestMapping("/v1/api/whisky")
@RequiredArgsConstructor
public class WhiskyController {

    private final WhiskyService whiskyService;

    // 위스키 단일 조회


}
