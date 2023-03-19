package com.zelusik.eatery.app.service;

import com.zelusik.eatery.app.domain.curation.Curation;
import com.zelusik.eatery.app.dto.curation.CurationDto;
import com.zelusik.eatery.app.repository.curation.CurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CurationService {

    private final CurationRepository curationRepository;

    @Transactional
    public CurationDto create(String title) {
        return CurationDto.from(curationRepository.save(Curation.of(title)));
    }
}
