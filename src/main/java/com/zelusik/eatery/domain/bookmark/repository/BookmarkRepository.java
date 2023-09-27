package com.zelusik.eatery.domain.bookmark.repository;

import com.zelusik.eatery.domain.bookmark.entity.Bookmark;
import com.zelusik.eatery.domain.place.entity.Place;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByMember_IdAndPlace(Long memberId, Place place);

    boolean existsByMember_IdAndPlace_Id(Long memberId, Long placeId);

    @EntityGraph(attributePaths = {"member", "place"})
    Optional<Bookmark> findByMember_IdAndPlace_Id(Long memberId, Long placeId);
}
