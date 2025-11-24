package org.whisky.domain.service;

import lombok.RequiredArgsConstructor;
import org.common.utils.OptionalUtil;
import org.common.utils.SuccessMessages;
import org.springframework.stereotype.Service;
import org.whisky.domain.dto.WhiskyResponse;
import org.whisky.domain.entity.Whisky;
import org.whisky.domain.repository.WhiskyRepository;

@Service
@RequiredArgsConstructor
public class WhiskyService {

    private final WhiskyRepository whiskyRepository;

    // 위스키 이름으로 단일 조회
    //todo 추후에 데이터 많을 시 n-gram 인덱스 기법 활용
    public WhiskyResponse findWhisky(final String keyword){
        Whisky whisky = OptionalUtil.getOrElseThrow(whiskyRepository.findBySearchKeyword(keyword), SuccessMessages.WHISKY_RETRIEVE_SUCCESS);
        return WhiskyResponse.toWhiskyDto(whisky);
    }

    // 카테고리별 조회
}
