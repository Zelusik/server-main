package com.zelusik.eatery.repository.review;

import com.zelusik.eatery.domain.review.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageRepository extends
        JpaRepository<ReviewImage, Long>,
        ReviewImageQuerydslRepository {
}
