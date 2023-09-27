package com.zelusik.eatery.domain.location.service;

import com.zelusik.eatery.domain.location.dto.LocationDto;
import com.zelusik.eatery.domain.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LocationService {

    private final LocationRepository locationRepository;

    /**
     * sido, sgg, emdg에 <code>keyword</code>가 포함된 location들을 조회한다.
     *
     * @param keyword  검색 키워드
     * @param pageable paging 정보
     * @return 조회된 location dtos
     */
    public Page<LocationDto> searchDtosByKeyword(String keyword, Pageable pageable) {
        return locationRepository.searchDtosByKeyword(keyword, pageable);
    }
}
