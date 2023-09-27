package com.zelusik.eatery.domain.place.repository;

import com.zelusik.eatery.domain.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends
        JpaRepository<Place, Long>,
        PlaceRepositoryQCustom,
        PlaceRepositoryJCustom {

    boolean existsByKakaoPid(String kakaoPid);

    Optional<Place> findByKakaoPid(String kakaoPid);
}
