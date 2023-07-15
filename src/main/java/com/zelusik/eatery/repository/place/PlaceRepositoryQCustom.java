package com.zelusik.eatery.repository.place;

import com.zelusik.eatery.dto.place.PlaceDto;

import java.util.Optional;

public interface PlaceRepositoryQCustom {

    Optional<PlaceDto> findDtoWithMarkedStatus(Long id, Long memberId);
}
