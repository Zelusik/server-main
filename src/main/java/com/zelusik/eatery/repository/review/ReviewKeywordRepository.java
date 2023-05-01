package com.zelusik.eatery.repository.review;

import com.zelusik.eatery.domain.review.ReviewKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewKeywordRepository extends
        JpaRepository<ReviewKeyword, Long>,
        ReviewKeywordRepositoryJCustom {
}
