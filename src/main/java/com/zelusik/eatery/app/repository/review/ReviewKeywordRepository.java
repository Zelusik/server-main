package com.zelusik.eatery.app.repository.review;

import com.zelusik.eatery.app.domain.review.ReviewKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewKeywordRepository extends
        JpaRepository<ReviewKeyword, Long>,
        ReviewKeywordJdbcTemplateRepository {
}
