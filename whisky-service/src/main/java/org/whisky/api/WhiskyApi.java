package org.whisky.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.whisky.domain.service.DataLoadInitializer;

@RestController
@RequiredArgsConstructor
public class WhiskyApi {
    private final DataLoadInitializer initializer;

    @PostMapping("/v1/api/whisky")
    public ResponseEntity<String> initWhisky() {
        int count = initializer.loadWhiskyData();
        return ResponseEntity.ok("총 " + count + "개의 위스키 데이터 적재 완료");
    }
}
