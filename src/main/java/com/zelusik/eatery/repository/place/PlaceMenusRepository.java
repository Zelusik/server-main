package com.zelusik.eatery.repository.place;

import com.zelusik.eatery.domain.place.PlaceMenus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceMenusRepository extends JpaRepository<PlaceMenus, Long> {

    // TODO: fetch join 여부 고려
    List<PlaceMenus> findAllByPlace_Id(Long placeId);
}
