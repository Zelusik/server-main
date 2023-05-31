package com.zelusik.eatery.repository.place;

import com.zelusik.eatery.domain.place.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends
        JpaRepository<Place, Long>,
        PlaceRepositoryQCustom,
        PlaceRepositoryJCustom {

    Optional<Place> findByKakaoPid(String kakaoPid);
}
