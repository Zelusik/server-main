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

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CurationService {

    private final PlaceService placeService;
    private final CurationElemFileService curationElemFileService;
    private final CurationRepository curationRepository;
    private final CurationElemRepository curationElemRepository;

    @Transactional
    public CurationDto create(String title) {
        return CurationDto.from(curationRepository.save(Curation.of(title)));
    }

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

    private Curation findEntityById(Long curationId) {
        return curationRepository.findById(curationId)
                .orElseThrow(CurationNotFoundException::new);
    }
}
