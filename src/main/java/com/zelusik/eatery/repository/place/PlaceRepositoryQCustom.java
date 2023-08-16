package com.zelusik.eatery.repository.place;

import com.zelusik.eatery.domain.place.Place;
import com.zelusik.eatery.dto.place.PlaceDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface PlaceRepositoryQCustom {

    Optional<PlaceDto> findDtoWithMarkedStatus(Long id, Long memberId);

    Slice<Place> searchByKeyword(String keyword, Pageable pageable);
}
