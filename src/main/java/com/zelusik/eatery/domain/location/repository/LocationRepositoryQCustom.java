package com.zelusik.eatery.domain.location.repository;

import com.zelusik.eatery.domain.location.dto.LocationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LocationRepositoryQCustom {

    /**
     * sido, sgg, emdg에 <code>keyword</code>가 포함된 location들을 조회한다.
     *
     * @param keyword  검색 키워드
     * @param pageable paging 정보
     * @return 조회된 location dtos
     */
    Page<LocationDto> searchDtosByKeyword(String keyword, Pageable pageable);
}
