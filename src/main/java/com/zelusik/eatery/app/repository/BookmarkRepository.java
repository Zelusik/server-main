package com.zelusik.eatery.app.repository;

import com.zelusik.eatery.app.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}
