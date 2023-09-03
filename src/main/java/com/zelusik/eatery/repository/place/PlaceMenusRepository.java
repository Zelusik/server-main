package com.zelusik.eatery.repository.place;

import com.zelusik.eatery.domain.place.PlaceMenus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceMenusRepository extends JpaRepository<PlaceMenus, Long> {

    boolean existsByPlace_Id(Long placeId);

    boolean existsByPlace_KakaoPid(String kakaoPid);

    Optional<PlaceMenus> findByPlace_Id(Long placeId);

    @EntityGraph(attributePaths = "place")
    Optional<PlaceMenus> findByPlace_KakaoPid(String kakaoPid);

    void deleteByPlace_Id(Long placeId);
}
