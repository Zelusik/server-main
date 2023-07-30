package com.zelusik.eatery.repository.place;

import com.zelusik.eatery.domain.place.PlaceMenus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceMenusRepository extends JpaRepository<PlaceMenus, Long> {

    Optional<PlaceMenus> findByPlace_Id(Long placeId);
}
