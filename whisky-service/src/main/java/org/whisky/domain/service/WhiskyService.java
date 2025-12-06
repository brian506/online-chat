package org.whisky.domain.service;

import lombok.RequiredArgsConstructor;
import org.common.utils.ErrorMessages;
import org.common.utils.OptionalUtil;
import org.common.utils.SuccessMessages;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.whisky.domain.dto.WhiskyResponse;
import org.whisky.domain.entity.Whisky;
import org.whisky.domain.repository.WhiskyRepository;

@Service
@RequiredArgsConstructor
public class WhiskyService {

    private final WhiskyRepository whiskyRepository;

    // 위스키 상세 조회 - 캐싱
    @Cacheable(value = "whisky", key = "#whiskyId")
    @Transactional(readOnly = true)
    public WhiskyResponse getWhisky(final String whiskyId){
        Whisky whisky = OptionalUtil.getOrElseThrow(whiskyRepository.findById(whiskyId), ErrorMessages.WHISKY_NOT_FOUND);
        return WhiskyResponse.toWhiskyDto(whisky);
    }

    // 위스키 이름으로 단일 조회
    //todo 추후에 데이터 많을 시 n-gram 인덱스 기법 활용
    public Slice<WhiskyResponse> findWhisky(final String keyword, Pageable pageable){
        return whiskyRepository.findBySearchKeyword(keyword,pageable)
                .map(WhiskyResponse::toWhiskyDto);
    }

    // 카테고리별 조회



}
