package com.zelusik.eatery.domain.review_keyword.repository;

import com.zelusik.eatery.domain.review_keyword.entity.ReviewKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewKeywordRepository extends
        JpaRepository<ReviewKeyword, Long>,
        ReviewKeywordRepositoryJCustom {
}
