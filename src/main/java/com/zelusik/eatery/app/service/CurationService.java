package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.curation.Curation;
import com.zelusik.eatery.app.domain.curation.CurationElem;
import com.zelusik.eatery.app.domain.curation.CurationElemFile;
import com.zelusik.eatery.app.domain.place.Place;
import com.zelusik.eatery.app.dto.curation.CurationDto;
import com.zelusik.eatery.app.dto.curation.request.CurationElemCreateRequest;
import com.zelusik.eatery.app.dto.place.request.PlaceCreateRequest;
import com.zelusik.eatery.app.repository.curation.CurationElemRepository;
import com.zelusik.eatery.app.repository.curation.CurationRepository;
import com.zelusik.eatery.global.exception.curation.CurationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Transactional
    public CurationDto create(String title) {
        return CurationDto.from(curationRepository.save(Curation.of(title)));
    }

    /**
     * 특정 큐레이션에 콘텐츠(CurationElem)을 추가합니다.
     *
     * @param curationId curation element를 추가하고자 하는 curation의 PK
     * @param curationElemCreateRequest 추가하고자 하는 curation element 정보
     * @return curation element가 추가된 curation dto
     */
    @Transactional
    public CurationDto addCurationElem(Long curationId, CurationElemCreateRequest curationElemCreateRequest) {
        PlaceCreateRequest placeCreateRequest = curationElemCreateRequest.getPlace();
        Place place = placeService.findOptEntityByKakaoPid(placeCreateRequest.getKakaoPid())
                .orElseGet(() -> placeService.create(placeCreateRequest));

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
    public Curation findEntityById(Long curationId) {
        return curationRepository.findById(curationId)
                .orElseThrow(CurationNotFoundException::new);
    }

    /**
     * 모든 큐레이션을 조회합니다.
     * 정렬 기준은 최신순입니다.
     *
     * @return 조회된 큐레이션 dto 목록
     */
    public List<CurationDto> findDtos() {
        return curationRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(curation -> curation.getCurationElems().size() > 0)
                .map(CurationDto::from)
                .toList();
    }
}
