package com.zelusik.eatery.app.repository.bookmark;

import com.zelusik.eatery.app.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends
        JpaRepository<Bookmark, Long>,
        BookmarkJdbcTemplateRepository {

    boolean existsByMember_IdAndPlace_Id(Long memberId, Long placeId);
}
