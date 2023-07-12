package com.zelusik.eatery.service;

import com.zelusik.eatery.domain.curation.Curation;
import com.zelusik.eatery.domain.curation.CurationElem;
import com.zelusik.eatery.domain.curation.CurationElemFile;
import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.dto.curation.CurationDto;
import com.zelusik.eatery.dto.curation.request.CurationElemCreateRequest;
import com.zelusik.eatery.dto.curation.response.CurationListResponse;
import com.zelusik.eatery.dto.curation.response.CurationResponse;
import com.zelusik.eatery.dto.place.request.PlaceCreateRequest;
import com.zelusik.eatery.exception.curation.CurationNotFoundException;
import com.zelusik.eatery.repository.curation.CurationElemRepository;
import com.zelusik.eatery.repository.curation.CurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CurationService {

    private final PlaceService placeService;
    private final CurationElemFileService curationElemFileService;
    private final CurationRepository curationRepository;
    private final CurationElemRepository curationElemRepository;

    /**
     * 큐레이션을 생성 및 저장합니다.
     *
     * @param title curation 제목
     * @return 생성된 curation dto
     */
    @CacheEvict(value = "curation", key = "'all'", cacheManager = "curationCacheManager")
    @Transactional
    public CurationDto create(String title) {
        return CurationDto.from(curationRepository.save(Curation.of(title)));
    }

    /**
     * 특정 큐레이션에 콘텐츠(CurationElem)을 추가합니다.
     *
     * @param curationId                curation element를 추가하고자 하는 curation의 PK
     * @param curationElemCreateRequest 추가하고자 하는 curation element 정보
     * @return curation element가 추가된 curation dto
     */
    @CacheEvict(value = "curation", key = "'all'", cacheManager = "curationCacheManager")
    @Transactional
    public CurationDto addCurationElem(Long memberId, Long curationId, CurationElemCreateRequest curationElemCreateRequest) {
        PlaceCreateRequest placeCreateRequest = curationElemCreateRequest.getPlace();
        Place place = placeService.findOptByKakaoPid(placeCreateRequest.getKakaoPid())
                .orElseGet(() -> {
                    Long createdPlaceId = placeService.create(memberId, placeCreateRequest).getId();
                    return placeService.findById(createdPlaceId);
                });

        Curation curation = findEntityById(curationId);

        CurationElemFile curationElemFile = curationElemFileService.upload(curationElemCreateRequest.getImage());

        CurationElem curationElem = CurationElem.of(curation, place, curationElemFile);
        curationElemRepository.save(curationElem);

        return CurationDto.from(curation);
    }

    /**
     * curationId에 해당하는 큐레이션을 조회합니다.
     *
     * @param curationId 조회하고자 하는 큐레이션의 PK
     * @return 조회한 큐레이션 entity
     */
    private Curation findEntityById(Long curationId) {
        return curationRepository.findById(curationId)
                .orElseThrow(CurationNotFoundException::new);
    }

    public CurationDto findDtoById(Long curationId) {
        return CurationDto.from(findEntityById(curationId));
    }

    /**
     * 모든 큐레이션을 조회합니다.
     * 정렬 기준은 최신순입니다.
     *
     * @return 조회된 큐레이션 dto 목록
     */
    private Stream<CurationDto> findDtoStream() {
        return curationRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(curation -> curation.getCurationElems().size() > 0)
                .map(CurationDto::from);
    }

    @Cacheable(value = "curation", key = "'all'", cacheManager = "curationCacheManager")
    public CurationListResponse getCurationList() {
        return CurationListResponse.of(findDtoStream()
                .map(CurationResponse::from)
                .toList());
    }
}
