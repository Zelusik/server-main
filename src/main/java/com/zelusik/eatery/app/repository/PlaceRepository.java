package com.zelusik.eatery.app.repository;

import com.zelusik.eatery.app.domain.place.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends
        JpaRepository<Place, Long>,
        PlaceJdbcTemplateRepository {

    Optional<Place> findByKakaoPid(String kakaoPid);
}
