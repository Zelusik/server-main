package com.zelusik.eatery.app.repository.review;

import com.zelusik.eatery.app.domain.review.ReviewFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewFileRepository extends
        JpaRepository<ReviewFile, Long>,
        ReviewFileQuerydslRepository
{
}
