package com.zelusik.eatery.repository.bookmark;

import com.zelusik.eatery.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends
        JpaRepository<Bookmark, Long>,
        BookmarkJdbcTemplateRepository {

    boolean existsByMember_IdAndPlace_Id(Long memberId, Long placeId);

    Optional<Bookmark> findByMember_IdAndPlace_Id(Long memberId, Long placeId);
}
