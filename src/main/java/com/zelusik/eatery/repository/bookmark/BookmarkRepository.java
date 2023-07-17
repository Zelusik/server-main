package com.zelusik.eatery.repository.bookmark;

import com.zelusik.eatery.domain.Bookmark;
import com.zelusik.eatery.domain.member.Member;
import com.zelusik.eatery.domain.place.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByMember_IdAndPlace(Long memberId, Place place);

    boolean existsByMember_IdAndPlace_Id(Long memberId, Long placeId);

    Optional<Bookmark> findByMember_IdAndPlace_Id(Long memberId, Long placeId);
}
