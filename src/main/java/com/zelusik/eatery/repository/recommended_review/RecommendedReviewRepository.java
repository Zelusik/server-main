package com.zelusik.eatery.repository.recommended_review;

import com.zelusik.eatery.domain.Location;
import com.zelusik.eatery.domain.RecommendedReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendedReviewRepository extends JpaRepository<RecommendedReview, Location> {
}
