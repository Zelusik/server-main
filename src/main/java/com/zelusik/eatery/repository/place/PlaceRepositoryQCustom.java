package com.zelusik.eatery.repository.place;

import com.zelusik.eatery.dto.place.PlaceDtoWithMarkedStatus;

import java.util.Optional;

public interface PlaceRepositoryQCustom {

    Optional<PlaceDtoWithMarkedStatus> findDtoWithMarkedStatus(Long id, Long memberId);
}
