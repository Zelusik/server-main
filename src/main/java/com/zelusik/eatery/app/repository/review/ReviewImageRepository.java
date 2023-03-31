package com.zelusik.eatery.app.repository.review;

import com.zelusik.eatery.app.domain.review.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageRepository extends
        JpaRepository<ReviewImage, Long>,
        ReviewImageQuerydslRepository
{
}
